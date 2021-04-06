package relaxeddd.simplediary.viewmodel

import io.ktor.util.date.*
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.RepeatRule
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.generateId
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TIME_DAY
import relaxeddd.simplediary.utils.TIME_WEEK
import relaxeddd.simplediary.utils.observable.Observable
import relaxeddd.simplediary.utils.observable.MutableObservable

internal abstract class ViewModelTaskList(private val repositoryTasks: RepositoryTasks) : ViewModelTask(repositoryTasks), IViewModelTaskList {

    internal open val isAddCurrentDayTask = true
    internal open val isAddIntermediateDayTasks = true

    private val tasksM: MutableObservable<List<Task>> = MutableObservable(ArrayList())
    override val tasks: Observable<List<Task>> get() = tasksM

    private val isVisibleTextNoItemsM = MutableObservable(true)
    override val isVisibleTextNoItems: Observable<Boolean> get() = isVisibleTextNoItemsM

    private val isVisibleTaskListM = MutableObservable(false)
    override val isVisibleTaskList: Observable<Boolean> get() = isVisibleTaskListM

    internal abstract fun filterRule(task: Task) : Boolean

    private val observerTasks: (List<Task>) -> Unit = {
        var tasks = ArrayList(it).filter(::filterRule)
        val childTasks = ArrayList<Task>()

        for (parentTask in tasks) {
            if (parentTask.repeat != RepeatRule.NO.ordinal && !parentTask.isPersistent) {
                val currentTimeMillis = getCurrentTime()
                val startTimeMillis = parentTask.start
                val duration = parentTask.end - startTimeMillis

                when(parentTask.repeat) {
                    RepeatRule.DAYLY.ordinal -> {
                        val interval = TIME_DAY
                        val daysToCurrentTime = (currentTimeMillis - startTimeMillis) / interval
                        val startTimeNearestTask = startTimeMillis + (daysToCurrentTime * interval)

                        for (childIndex in -20..20) {
                            val childTaskStart = startTimeNearestTask + (childIndex * interval)
                            if (childTaskStart > startTimeMillis && !parentTask.exDates.contains(childTaskStart)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskStart, childTaskStart + duration))
                            }
                        }
                    }
                    RepeatRule.WEEKLY.ordinal -> {
                        val interval = TIME_WEEK
                        val weeksToCurrentTime = (currentTimeMillis - startTimeMillis) / interval
                        val startTimeNearestTask = startTimeMillis + (weeksToCurrentTime * interval)

                        for (childIndex in -4..4) {
                            val childTaskStart = startTimeNearestTask + (childIndex * interval)
                            if (childTaskStart > startTimeMillis && !parentTask.exDates.contains(childTaskStart)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskStart, childTaskStart + duration))
                            }
                        }
                    }
                    RepeatRule.MONTHLY.ordinal -> {
                        val date = GMTDate(startTimeMillis)
                        val currentDate = GMTDate(currentTimeMillis)
                        val currentMonth = currentDate.month
                        val currentYear = currentDate.year

                        for (childIndex in -4..4) {
                            var month = currentMonth.ordinal + childIndex
                            var yearIncrement = 0

                            while(month < 0) {
                                month += 12
                                yearIncrement -= 1
                            }
                            while(month >= Month.values().size) {
                                month -= 12
                                yearIncrement += 1
                            }

                            val childTaskStart = GMTDate(date.seconds, date.minutes, date.hours, date.dayOfMonth, Month.values()[month], currentYear + yearIncrement)
                            val childTaskMillis = childTaskStart.timestamp

                            if (childTaskMillis > startTimeMillis && !parentTask.exDates.contains(childTaskMillis)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskMillis, childTaskMillis + duration))
                            }
                        }
                    }
                    RepeatRule.YEARLY.ordinal -> {
                        val date = GMTDate(startTimeMillis)
                        val currentDate = GMTDate(currentTimeMillis)
                        val currentYear = currentDate.year

                        for (childIndex in -1..2) {
                            val childTaskStart = GMTDate(date.seconds, date.minutes, date.hours, date.dayOfMonth, date.month, currentYear + childIndex)
                            val childTaskMillis = childTaskStart.timestamp

                            if (childTaskMillis > startTimeMillis && !parentTask.exDates.contains(childTaskMillis)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskMillis, childTaskMillis + duration))
                            }
                        }
                    }
                }
            }
        }

        childTasks.addAll(tasks)
        tasks = childTasks.sortedBy { task -> task.start }

        if (isAddIntermediateDayTasks) {
            val tasksWithDays = ArrayList<Task>()
            for ((ix, task) in tasks.withIndex()) {
                var previousTaskIndex = ix - 1
                var previousTask: Task? = null

                while ((previousTask == null || previousTask.isHidden()) && previousTaskIndex >= 0) {
                    previousTask = tasks[previousTaskIndex]
                    previousTaskIndex -= 1
                }

                if (previousTask != null && !previousTask.isHidden()) {
                    val startDay = previousTask.start / TIME_DAY * TIME_DAY
                    val dayDifference = ((task.start / TIME_DAY * TIME_DAY) - startDay) / TIME_DAY
                    for (dayIx in 1 until dayDifference) {
                        tasksWithDays.add(Task(generateId(), startDate = startDay + dayIx * TIME_DAY, isDateTask = true))
                    }
                }
                tasksWithDays.add(task)
            }
            tasks = ArrayList(tasksWithDays)
        }

        if (isAddCurrentDayTask) {
            val currentDayTask = tasks.find { it.start / TIME_DAY * TIME_DAY == getCurrentTime() / TIME_DAY * TIME_DAY }
            if (currentDayTask == null || currentDayTask.isHidden()) {
                val tasksWithCurrentDay = ArrayList(tasks)
                tasksWithCurrentDay.add(Task(generateId(), startDate = getCurrentTime(), isDateTask = true))
                tasks = ArrayList(tasksWithCurrentDay)
            }
        }

        tasks = sortTasks(tasks)

        tasksM.value = tasks
        isVisibleTaskListM.value = tasks.isNotEmpty()
        isVisibleTextNoItemsM.postValue(tasks.isEmpty() && !isVisibleProgressBar.value)
    }

    private val observerLoading: (Boolean) -> Unit = {
        isVisibleTextNoItemsM.postValue(tasks.value.isEmpty() && !isVisibleProgressBar.value)
    }

    private val observerException: (Throwable?) -> Unit = { exception ->
        if (exception != null) {
            isVisibleTaskListM.postValue(false)
            isVisibleTextNoItemsM.postValue(true)
            actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, exception.toString()))))
        }
    }

    override fun onFill() {
        super.onFill()
        isVisibleProgressBar.addObserver(observerLoading)
        repositoryTasks.tasks.addObserver(observerTasks)
        repositoryTasks.exception.addObserver(observerException)
    }

    override fun onCleared() {
        super.onCleared()
        tasksM.removeAllObservers()
        isVisibleTextNoItemsM.removeAllObservers()
        isVisibleTaskListM.removeAllObservers()

        isVisibleProgressBar.removeObserver(observerLoading)
        repositoryTasks.tasks.removeObserver(observerTasks)
        repositoryTasks.exception.removeObserver(observerException)
    }

    override fun load() {
        isVisibleProgressBarM.value = true
        repositoryTasks.init {
            isVisibleProgressBarM.value = false
        }
    }

    override fun deleteTask(id: String) {
        isVisibleProgressBarM.value = true
        repositoryTasks.deleteTask(id) {
            isVisibleProgressBarM.value = false
        }
    }

    protected open fun sortTasks(tasks: List<Task>) = tasks.sortedBy { it.start }
}
