package relaxeddd.simplediary.viewmodel

import io.ktor.util.date.*
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.RepeatRule
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.generateId
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TIME_DAY
import relaxeddd.simplediary.utils.TIME_WEEK
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

abstract class ViewModelTaskList : ViewModelTask() {

    private val tasksM: MutableLiveData<List<Task>> = MutableLiveData(ArrayList())
    val tasks: LiveData<List<Task>> = tasksM

    private val isVisibleTextNoItemsM = MutableLiveData(true)
    val isVisibleTextNoItems: LiveData<Boolean> = isVisibleTextNoItemsM

    private val isVisibleTaskListM = MutableLiveData(false)
    val isVisibleTaskList: LiveData<Boolean> = isVisibleTaskListM

    abstract fun filterRule(task: Task) : Boolean

    private val observerTasks: (List<Task>) -> Unit = {
        var tasks = ArrayList(it).filter(::filterRule)
        val childTasks = ArrayList<Task>()

        for (parentTask in tasks) {
            if (parentTask.rrule.isNotBlank()) {
                val currentTimeMillis = getCurrentTime()
                val startTimeMillis = parentTask.start
                val duration = parentTask.end - startTimeMillis

                when(parentTask.rrule) {
                    RepeatRule.DAYLY.toString() -> {
                        val interval = TIME_DAY
                        val daysToCurrentTime = (currentTimeMillis - startTimeMillis) / interval
                        val startTimeNearestTask = startTimeMillis + (daysToCurrentTime * interval)

                        for (childIndex in -100..100) {
                            val childTaskStart = startTimeNearestTask + (childIndex * interval)
                            if (childTaskStart > startTimeMillis && !parentTask.exDates.contains(childTaskStart)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskStart, childTaskStart + duration))
                            }
                        }
                    }
                    RepeatRule.WEEKLY.toString() -> {
                        val interval = TIME_WEEK
                        val weeksToCurrentTime = (currentTimeMillis - startTimeMillis) / interval
                        val startTimeNearestTask = startTimeMillis + (weeksToCurrentTime * interval)

                        for (childIndex in -12..12) {
                            val childTaskStart = startTimeNearestTask + (childIndex * interval)
                            if (childTaskStart > startTimeMillis && !parentTask.exDates.contains(childTaskStart)) {
                                childTasks.add(Task(parentTask, generateId(), childTaskStart, childTaskStart + duration))
                            }
                        }
                    }
                    RepeatRule.MONTHLY.toString() -> {
                        val date = GMTDate(startTimeMillis)
                        val currentDate = GMTDate(currentTimeMillis)
                        val currentMonth = currentDate.month
                        val currentYear = currentDate.year

                        for (childIndex in -12..12) {
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
                    RepeatRule.YEARLY.toString() -> {
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

    fun load() {
        isVisibleProgressBarM.value = true
        repositoryTasks.init {
            isVisibleProgressBarM.value = false
        }
    }

    fun deleteTask(id: String) {
        isVisibleProgressBarM.value = true
        repositoryTasks.deleteTask(id) {
            isVisibleProgressBarM.value = false
        }
    }
}
