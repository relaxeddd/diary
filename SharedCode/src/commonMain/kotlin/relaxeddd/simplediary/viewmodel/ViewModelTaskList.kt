package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

abstract class ViewModelTaskList : ViewModelTask() {

    private val repositoryUsers = repoUsers

    private val tasksM: MutableLiveData<List<Task>> = MutableLiveData(ArrayList())
    val tasks: LiveData<List<Task>> = tasksM

    private val isVisibleTextNoItemsM = MutableLiveData(true)
    val isVisibleTextNoItems: LiveData<Boolean> = isVisibleTextNoItemsM

    private val isVisibleTaskListM = MutableLiveData(false)
    val isVisibleTaskList: LiveData<Boolean> = isVisibleTaskListM

    abstract fun filterRule(task: Task) : Boolean

    private val observerTasks: (List<Task>) -> Unit = {
        val tasks = ArrayList(it).filter(::filterRule).sortedBy { task -> task.start }

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

    fun deleteTask(id: Long) {
        isVisibleProgressBarM.value = true
        repositoryTasks.deleteTask(id) {
            isVisibleProgressBarM.value = false
        }
    }

    fun onClickedLogout() {
        isVisibleProgressBarM.value = true
        repositoryUsers.signOut { isSuccess ->
            if (isSuccess) {
                isVisibleProgressBarM.value = false
                actionM.postValue(Action(EventType.GO_SCREEN_LOGIN))
            } else {
                actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, "Logout error")))) //TODO translation
            }
        }
    }
}
