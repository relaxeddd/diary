package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.RepeatRule
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.generateId
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TIME_15_MINUTE
import relaxeddd.simplediary.utils.TIME_DAY
import relaxeddd.simplediary.utils.observable.Observable
import relaxeddd.simplediary.utils.observable.MutableObservable

internal class ViewModelTaskCard(private val repositoryTasks: RepositoryTasks) : ViewModelTask(repositoryTasks), IViewModelTaskCard {

    companion object {
        private const val DEFAULT_PRIORITY = 0
    }

    private var editTaskId: String? = null
    private var startDate: Long = 0

    private val isEnabledButtonSaveM = MutableObservable(false)
    override val isEnabledButtonSave: Observable<Boolean> get() = isEnabledButtonSaveM

    private val taskTitleM = MutableObservable("")
    override val taskTitle: Observable<String> get() = taskTitleM

    private val taskDescM = MutableObservable("")
    override val taskDesc: Observable<String> get() = taskDescM

    private val taskCommentM = MutableObservable("")
    override val taskComment: Observable<String> get() = taskCommentM

    private val taskLocationM = MutableObservable("")
    override val taskLocation: Observable<String> get() = taskLocationM

    private val taskPriorityM = MutableObservable(DEFAULT_PRIORITY)
    override val taskPriority: Observable<Int> get() = taskPriorityM

    private val taskRepeatM = MutableObservable(RepeatRule.NO.ordinal)
    override val taskRepeat: Observable<Int> get() = taskRepeatM

    private val taskRepeatCountM = MutableObservable(0)
    override val taskRepeatCount: Observable<Int> get() = taskRepeatCountM

    private val taskStartM = MutableObservable(getCurrentTime())
    override val taskStart: Observable<Long> get() = taskStartM

    private val taskEndM = MutableObservable(getCurrentTime() + TIME_15_MINUTE)
    override val taskEnd: Observable<Long> get() = taskEndM

    private val taskUntilM = MutableObservable(0L)
    override val taskUntil: Observable<Long> get() = taskUntilM

    private val taskIsPersistentM = MutableObservable(false)
    override val taskIsPersistent: Observable<Boolean> get() = taskIsPersistentM

    private val taskIsCompletedM = MutableObservable(false)
    override val taskIsCompleted: Observable<Boolean> get() = taskIsCompletedM

    private val taskExDatesM = MutableObservable<List<Long>>(ArrayList())
    override val taskExDates: Observable<List<Long>> get() = taskExDatesM

    private val taskRemindHoursM = MutableObservable<List<Int>>(ArrayList())
    override val taskRemindHours: Observable<List<Int>> get() = taskRemindHoursM

    private val observerTitle: (String) -> Unit = {
        isEnabledButtonSaveM.value = it.isNotEmpty()
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onFill() {
        super.onFill()
        taskTitle.addObserver(observerTitle)
    }

    override fun onCleared() {
        super.onCleared()
        isEnabledButtonSaveM.removeAllObservers()
        taskTitleM.removeAllObservers()
        taskDescM.removeAllObservers()
        taskCommentM.removeAllObservers()
        taskLocationM.removeAllObservers()
        taskPriorityM.removeAllObservers()
        taskRepeatM.removeAllObservers()
        taskRepeatCountM.removeAllObservers()
        taskStartM.removeAllObservers()
        taskEndM.removeAllObservers()
        taskUntilM.removeAllObservers()
        taskIsPersistentM.removeAllObservers()
        taskIsCompletedM.removeAllObservers()
        taskRemindHoursM.removeAllObservers()
        taskExDatesM.removeAllObservers()

        taskTitle.removeObserver(observerTitle)
    }

    override fun load(editTaskParentId: String?, startDate: Long) {
        this.editTaskId = editTaskParentId
        this.startDate = startDate
        val editTask = editTaskParentId?.let { parentId -> repositoryTasks.tasks.value.find { it.id == parentId } }

        if (editTask != null) {
            val duration = editTask.end - editTask.start
            val endDate = startDate + duration

            taskTitleM.value = editTask.title
            taskDescM.value = editTask.desc
            taskCommentM.value = editTask.comment
            taskLocationM.value = editTask.location
            taskPriorityM.value = editTask.priority
            taskRepeatM.value = editTask.repeat
            taskRepeatCountM.value = editTask.repeatCount
            taskStartM.value = startDate
            taskEndM.value = endDate
            taskUntilM.value = editTask.untilDate
            taskIsPersistentM.value = editTask.isPersistent
            taskIsCompletedM.value = editTask.isCompleted
            taskRemindHoursM.value = editTask.remindHours
            taskExDatesM.value = editTask.exDates
            isEnabledButtonSaveM.value = true
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onClickedSave() {
        if (editTaskId != null) {
            val isCompleted = taskIsCompleted.value

            if (isCompleted) {
                val editTask = repositoryTasks.tasks.value.find { it.id == editTaskId }
                if (editTask?.isCompleted != isCompleted && editTask?.isRepetitive() == true) {
                    actionM.postValue(Action(EventType.NAVIGATION_DIALOG_REPETITIVE_TASK_COMPLETE))
                    return
                }
            }
        }

        saveOrCreateTask()
    }

    override fun onClickedCompleteChildTask() {
        val parentTask = repositoryTasks.tasks.value.find { it.id == editTaskId }

        if (parentTask != null && startDate != 0L) {
            val duration = parentTask.end - parentTask.start
            val childEndDate = startDate + duration

            val title = taskTitle.value
            val desc = taskDesc.value
            val comment = taskComment.value
            val location = taskLocation.value
            val priority = taskPriority.value
            val repeat = taskRepeat.value
            val repeatCount = taskRepeatCount.value
            val start = taskStart.value
            val end = taskEnd.value
            val until = taskUntil.value
            val isPersistent = taskIsPersistent.value
            val remindHours = taskRemindHours.value

            createTask(generateId(), title, desc, comment, location, priority, repeat, repeatCount, start, end,
                       until, isPersistent, true, remindHours) {
                val exDates = ArrayList(parentTask.exDates).apply { add(startDate) }

                updateTask(parentTask.id, parentTask.title, parentTask.desc, parentTask.comment,
                           parentTask.location, parentTask.priority, parentTask.repeat, parentTask.repeatCount,
                           parentTask.start, parentTask.end, parentTask.untilDate, parentTask.isPersistent,
                           false, exDates, parentTask.remindHours) { response: Response<List<Task>> ->
                    if (response.isValid) {
                        actionM.postValue(Action(EventType.EXIT))
                    } else {
                        actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, response.exception.toString()))))
                    }
                }
            }
        } else {
            saveOrCreateTask()
        }
    }

    override fun onClickedCompleteParentTask() {
        saveOrCreateTask()
    }

    override fun onClickedCancel() {
        actionM.postValue(Action(EventType.EXIT))
    }

    override fun onChangedTitle(value: String) {
        if (taskTitleM.value != value) {
            taskTitleM.postValue(value)
        }
    }

    override fun onChangedDesc(value: String) {
        if (taskDescM.value != value) {
            taskDescM.postValue(value)
        }
    }

    override fun onChangedComment(value: String) {
        if (taskCommentM.value != value) {
            taskCommentM.postValue(value)
        }
    }

    override fun onChangedLocation(value: String) {
        if (taskLocationM.value != value) {
            taskLocationM.postValue(value)
        }
    }

    override fun onChangedPriority(value: Int) {
        if (taskPriorityM.value != value) {
            taskPriorityM.postValue(value)
        }
    }

    override fun onChangedRepeat(value: Int) {
        if (taskRepeatM.value != value) {
            taskRepeatM.postValue(value)
        }
    }

    override fun onChangedRepeatCount(value: Int) {
        if (taskRepeatCountM.value != value) {
            taskRepeatCountM.postValue(value)
        }
        if (value != 0) {
            onChangedUntil(0)
        }
    }

    override fun onChangedStart(value: Long) {
        val startDaysInMillis = value / TIME_DAY * TIME_DAY
        val startTime = value - startDaysInMillis
        val endDaysInMillis = taskEndM.value / TIME_DAY * TIME_DAY
        var endTime = taskEndM.value - endDaysInMillis

        if (startDaysInMillis + startTime >= startDaysInMillis + endTime) {
            endTime = startTime + TIME_15_MINUTE
        }
        if (taskEndM.value != startDaysInMillis + endTime) {
            taskEndM.postValue(startDaysInMillis + endTime)
        }
        if (taskStartM.value != value) {
            taskStartM.postValue(value)
        }
    }

    override fun onChangedEnd(value: Long) {
        val startDaysInMillis = taskStartM.value / TIME_DAY * TIME_DAY
        var startTime = taskStartM.value - startDaysInMillis
        val endDaysInMillis = value / TIME_DAY * TIME_DAY
        val endTime = value - endDaysInMillis

        if (taskStartM.value >= value) {
            taskStartM.postValue(value - TIME_15_MINUTE)
        }

        if (endDaysInMillis + startTime >= endDaysInMillis + endTime) {
            startTime = endTime - TIME_15_MINUTE
        }
        if (taskStartM.value != endDaysInMillis + startTime) {
            taskStartM.postValue(endDaysInMillis + startTime)
        }
        if (taskEndM.value != value) {
            taskEndM.postValue(value)
        }
    }

    override fun onChangedUntil(value: Long) {
        if (taskUntilM.value != value) {
            taskUntilM.postValue(value)
        }
        if (value != 0L) {
            onChangedRepeat(0)
        }
    }

    override fun onChangedIsPersistent(value: Boolean) {
        if (taskIsPersistentM.value != value) {
            taskIsPersistentM.postValue(value)
        }
    }

    override fun onChangedIsCompleted(value: Boolean) {
        if (taskIsCompletedM.value != value) {
            taskIsCompletedM.postValue(value)
        }
    }

    override fun onAddRemindHour(value: Int) {
        if (!taskRemindHoursM.value.contains(value)) {
            taskRemindHoursM.value = ArrayList(taskRemindHoursM.value).apply { add(value) }
        }
    }

    override fun onRemoveRemindHour(value: Int) {
        if (taskRemindHoursM.value.contains(value)) {
            taskRemindHoursM.value = ArrayList(taskRemindHoursM.value).apply { remove(value) }
        }
    }

    private fun saveOrCreateTask() {
        val taskId = this.editTaskId
        val title = taskTitle.value
        val desc = taskDesc.value
        val comment = taskComment.value
        val location = taskLocation.value
        val priority = taskPriority.value
        val repeat = taskRepeat.value
        val repeatCount = taskRepeatCount.value
        val start = taskStart.value
        val end = taskEnd.value
        val until = taskUntil.value
        val isPersistent = taskIsPersistent.value
        val isCompleted = taskIsCompleted.value
        val exDates = taskExDates.value
        val remindHours = taskRemindHours.value
        val callback = { response: Response<List<Task>> ->
            if (response.isValid) {
                actionM.postValue(Action(EventType.EXIT))
            } else {
                actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, response.exception.toString()))))
            }
        }

        if (taskId != null) {
            updateTask(taskId, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                       isPersistent, isCompleted, exDates, remindHours, callback)
        } else {
            createTask(generateId(), title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                       isPersistent, isCompleted, remindHours, callback)
        }
    }
}
