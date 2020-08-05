package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.launchSilent

class ViewModelTaskList : ViewModelBase() {

    private val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()

    private val tasksM: MutableLiveData<List<Task>> = MutableLiveData(ArrayList())
    val tasks: LiveData<List<Task>> = tasksM

    private val isVisibleTextNoItemsM = MutableLiveData(true)
    val isVisibleTextNoItems: LiveData<Boolean> = isVisibleTextNoItemsM

    private val isVisibleTaskListM = MutableLiveData(false)
    val isVisibleTaskList: LiveData<Boolean> = isVisibleTaskListM

    private val observerTasks: (List<Task>) -> Unit = {
        tasksM.postValue(it.sortedBy { task -> task.startDate })
        isVisibleTaskListM.postValue(it.isNotEmpty())
        isVisibleTextNoItemsM.postValue(it.isEmpty() && !isVisibleProgressBar.value)
    }

    private val observerLoading: (Boolean) -> Unit = {
        isVisibleTextNoItemsM.postValue(tasks.value.isEmpty() && !isVisibleProgressBar.value)
    }

    private val observerException: (Throwable?) -> Unit = { exception ->
        if (exception != null) {
            isVisibleTaskListM.postValue(false)
            isVisibleTextNoItemsM.postValue(true)
            actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, exception.message ?: ""))))
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

    fun load() = launchSilent(coroutineContext, exceptionHandler, job) {
        operationWithLoading(repositoryTasks::init)
    }

    fun deleteTask(id: Long) = launchSilent(coroutineContext, exceptionHandler, job) {
        operationWithLoading { repositoryTasks.deleteTask(id) }
    }
}
