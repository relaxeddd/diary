package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.Task
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

    private var editTaskId: Long? = null

    private val isEnabledButtonSaveM = MutableLiveData(false)
    val isEnabledButtonSave: LiveData<Boolean> = isEnabledButtonSaveM

    private val taskTitleM = MutableLiveData("")
    val taskTitle: LiveData<String> = taskTitleM

    private val taskDescM = MutableLiveData("")
    val taskDesc: LiveData<String> = taskDescM

    private val taskPriorityM = MutableLiveData(DEFAULT_PRIORITY)
    val taskPriority: LiveData<Int> = taskPriorityM

    private val taskStartM = MutableLiveData(getCurrentTime())
    val taskStart: LiveData<Long> = taskStartM

    private val taskEndM = MutableLiveData(getCurrentTime() + TIME_15_MINUTE)
    val taskEnd: LiveData<Long> = taskEndM

    private val taskIsCompletedM = MutableLiveData(false)
    val taskIsCompleted: LiveData<Boolean> = taskIsCompletedM

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
        taskPriorityM.removeAllObservers()
        taskStartM.removeAllObservers()
        taskEndM.removeAllObservers()
        taskIsCompletedM.removeAllObservers()

        taskTitle.removeObserver(observerTitle)
    }

    fun load(editTaskId: Long?) {
        this.editTaskId = editTaskId
        if (editTaskId != null) {
            val editTask = repositoryTasks.tasks.value.find { it.id == editTaskId }
            taskTitleM.value = editTask?.title ?: ""
            taskDescM.value = editTask?.desc ?: ""
            taskPriorityM.value = editTask?.priority ?: DEFAULT_PRIORITY
            taskStartM.value = editTask?.start ?: 0L
            taskEndM.value = editTask?.end ?: 0L
            taskIsCompletedM.value = editTask?.isCompleted ?: false
            isEnabledButtonSaveM.value = true
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    fun onClickedSave() {
        val taskId = this.editTaskId
        val title = taskTitle.value
        val desc = taskDesc.value
        val priority = taskPriority.value
        val rrule: String? = null
        val location: String? = null
        val start: Long = taskStart.value
        val end: Long = taskEnd.value
        val isCompleted: Boolean = taskIsCompleted.value
        val callback = { response: Response<List<Task>> ->
            if (response.isValid) {
                actionM.postValue(Action(EventType.EXIT))
            } else {
                actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, response.exception.toString()))))
            }
        }

        if (taskId != null) {
            updateTask(taskId, title, desc, priority, rrule, location, start, end, isCompleted, callback)
        } else {
            createTask(title, desc, priority, rrule, location, start, end, isCompleted, callback)
        }
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

    fun onChangedPriority(value: Int) {
        if (taskPriorityM.value != value) {
            taskPriorityM.postValue(value)
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

    fun onChangedIsCompleted(value: Boolean) {
        if (taskIsCompletedM.value != value) {
            taskIsCompletedM.postValue(value)
        }
    }
}
