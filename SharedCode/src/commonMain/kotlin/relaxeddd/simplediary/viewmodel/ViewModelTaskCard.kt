package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.RepeatRule
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.generateId
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TIME_15_MINUTE
import relaxeddd.simplediary.utils.TIME_DAY
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class ViewModelTaskCard : ViewModelTask() {

    companion object {
        private const val DEFAULT_PRIORITY = 0
    }

    private var editTaskId: String? = null
    private var startDate: Long = 0

    private val isEnabledButtonSaveM = MutableLiveData(false)
    val isEnabledButtonSave: LiveData<Boolean> = isEnabledButtonSaveM

    private val taskTitleM = MutableLiveData("")
    val taskTitle: LiveData<String> = taskTitleM

    private val taskDescM = MutableLiveData("")
    val taskDesc: LiveData<String> = taskDescM

    private val taskCommentM = MutableLiveData("")
    val taskComment: LiveData<String> = taskCommentM

    private val taskLocationM = MutableLiveData("")
    val taskLocation: LiveData<String> = taskLocationM

    private val taskPriorityM = MutableLiveData(DEFAULT_PRIORITY)
    val taskPriority: LiveData<Int> = taskPriorityM

    private val taskRepeatM = MutableLiveData(RepeatRule.NO.ordinal)
    val taskRepeat: LiveData<Int> = taskRepeatM

    private val taskRepeatCountM = MutableLiveData(0)
    val taskRepeatCount: LiveData<Int> = taskRepeatCountM

    private val taskStartM = MutableLiveData(getCurrentTime())
    val taskStart: LiveData<Long> = taskStartM

    private val taskEndM = MutableLiveData(getCurrentTime() + TIME_15_MINUTE)
    val taskEnd: LiveData<Long> = taskEndM

    private val taskUntilM = MutableLiveData(0L)
    val taskUntil: LiveData<Long> = taskUntilM

    private val taskIsPersistentM = MutableLiveData(false)
    val taskIsPersistent: LiveData<Boolean> = taskIsPersistentM

    private val taskIsCompletedM = MutableLiveData(false)
    val taskIsCompleted: LiveData<Boolean> = taskIsCompletedM

    private val taskExDatesM = MutableLiveData<List<Long>>(ArrayList())
    val taskExDates: LiveData<List<Long>> = taskExDatesM

    private val taskRemindHoursM = MutableLiveData<List<Int>>(ArrayList())
    val taskRemindHours: LiveData<List<Int>> = taskRemindHoursM

    private val observerTitle: (String) -> Unit = {
        isEnabledButtonSaveM.value = it.isNotEmpty()
    }

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

    fun load(editTaskParentId: String?, startDate: Long) {
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
    fun onClickedSave() {
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

    fun onClickedCompleteChildTask() {
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

    fun onClickedCompleteParentTask() {
        saveOrCreateTask()
    }

    fun onClickedCancel() {
        actionM.postValue(Action(EventType.EXIT))
    }

    fun onChangedTitle(value: String) {
        if (taskTitleM.value != value) {
            taskTitleM.postValue(value)
        }
    }

    fun onChangedDesc(value: String) {
        if (taskDescM.value != value) {
            taskDescM.postValue(value)
        }
    }

    fun onChangedComment(value: String) {
        if (taskCommentM.value != value) {
            taskCommentM.postValue(value)
        }
    }

    fun onChangedLocation(value: String) {
        if (taskLocationM.value != value) {
            taskLocationM.postValue(value)
        }
    }

    fun onChangedPriority(value: Int) {
        if (taskPriorityM.value != value) {
            taskPriorityM.postValue(value)
        }
    }

    fun onChangedRepeat(value: Int) {
        if (taskRepeatM.value != value) {
            taskRepeatM.postValue(value)
        }
    }

    fun onChangedRepeatCount(value: Int) {
        if (taskRepeatCountM.value != value) {
            taskRepeatCountM.postValue(value)
        }
        if (value != 0) {
            onChangedUntil(0)
        }
    }

    fun onChangedStart(value: Long) {
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

    fun onChangedEnd(value: Long) {
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

    fun onChangedUntil(value: Long) {
        if (taskUntilM.value != value) {
            taskUntilM.postValue(value)
        }
        if (value != 0L) {
            onChangedRepeat(0)
        }
    }

    fun onChangedIsPersistent(value: Boolean) {
        if (taskIsPersistentM.value != value) {
            taskIsPersistentM.postValue(value)
        }
    }

    fun onChangedIsCompleted(value: Boolean) {
        if (taskIsCompletedM.value != value) {
            taskIsCompletedM.postValue(value)
        }
    }

    fun onAddRemindHour(value: Int) {
        if (!taskRemindHoursM.value.contains(value)) {
            taskRemindHoursM.value = ArrayList(taskRemindHoursM.value).apply { add(value) }
        }
    }

    fun onRemoveRemindHour(value: Int) {
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
