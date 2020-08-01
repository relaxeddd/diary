package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.launchSilent

class ViewModelTaskCard : ViewModelBase() {

    companion object {
        private const val DEFAULT_PRIORITY = 0
    }

    private val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()
    private var editTaskId: Long? = null

    private val taskTitleM = MutableLiveData("")
    val taskTitle: LiveData<String> = taskTitleM

    private val taskDescM = MutableLiveData("")
    val taskDesc: LiveData<String> = taskDescM

    private val taskPriorityM = MutableLiveData(DEFAULT_PRIORITY)
    val taskPriority: LiveData<Int> = taskPriorityM

    private val isEnabledButtonSaveM = MutableLiveData(false)
    val isEnabledButtonSave: LiveData<Boolean> = isEnabledButtonSaveM

    private val observerTitle: (String) -> Unit = {
        isEnabledButtonSaveM.value = it.isNotEmpty()
    }

    init {
        taskTitle.addObserver(observerTitle)
    }

    override fun onCleared() {
        super.onCleared()
        taskTitle.removeObserver(observerTitle)
    }

    fun load(editTaskId: Long?) {
        this.editTaskId = editTaskId
        if (editTaskId != null) {
            val editTask = repositoryTasks.tasks.value.find { it.id == editTaskId }
            taskTitleM.value =  editTask?.title ?: ""
            taskDescM.value =  editTask?.desc ?: ""
            taskPriorityM.value =  editTask?.priority ?: DEFAULT_PRIORITY
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
        val startDate: Long? = null
        val endDate: Long? = null

        if (taskId != null) {
            updateTask(taskId, title, desc, priority, rrule, location, startDate, endDate)
        } else {
            createTask(title, desc, priority, rrule, location, startDate, endDate)
        }
    }

    fun onClickedCancel() {
        actionM.postValue(Action(EventType.EXIT))
    }

    fun onChangedTitle(value: String) {
        taskTitleM.postValue(value)
    }

    fun onChangedDesc(value: String) {
        taskDescM.postValue(value)
    }

    fun onChangedPriority(value: Int) {
        taskPriorityM.postValue(value)
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun createTask(title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                           startDate: Long?, endDate: Long?) = launchSilent(coroutineContext, exceptionHandler, job) {
        performTaskOperation {
            repositoryTasks.createTask(title, desc, priority, rrule, location, startDate, endDate)
        }
    }

    private fun updateTask(id: Long, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                           startDate: Long?, endDate: Long?) = launchSilent(coroutineContext, exceptionHandler, job) {
        performTaskOperation {
            repositoryTasks.updateTask(id, title, desc, priority, rrule, location, startDate, endDate)
        }
    }

    private suspend fun performTaskOperation(operation: suspend () -> Response<Unit>) {
        var response: Response<Unit>? = null

        operationWithLoading {
            response = operation()
        }
        if (response?.isValid == true) {
            actionM.postValue(Action(EventType.EXIT))
        } else {
            actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, response?.exception?.message ?: ""))))
        }
    }
}