package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.utils.ERROR_TEXT

abstract class ViewModelTaskList : ViewModelTask() {

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

    init {
        isVisibleProgressBar.addObserver(observerLoading)
        repositoryTasks.tasks.addObserver(observerTasks)
        repositoryTasks.exception.addObserver(observerException)
    }

    override fun onCleared() {
        super.onCleared()
        isVisibleProgressBar.removeObserver(observerLoading)
        repositoryTasks.tasks.removeObserver(observerTasks)
        repositoryTasks.exception.removeObserver(observerException)
    }

    fun load() {
        operationWithLoading(repositoryTasks::init)
    }

    fun deleteTask(id: Long) {
        operationWithLoading { repositoryTasks.deleteTask(id) }
    }
}
