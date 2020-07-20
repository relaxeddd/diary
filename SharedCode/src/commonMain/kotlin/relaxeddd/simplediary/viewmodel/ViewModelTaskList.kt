package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.launchSilent

class ViewModelTaskList : ViewModelBase() {

    private val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()

    val state = MutableLiveData<TaskListState>(NotLoadedTaskListState())
    val isVisibleTextNoItems = MutableLiveData(true)
    val isVisibleTaskList = MutableLiveData(true)

    init {
        repositoryTasks.tasks.addObserver {
            isVisibleTaskList.postValue(it.isNotEmpty())
            isVisibleTextNoItems.postValue(it.isEmpty())
            state.postValue(SuccessTaskListState(Response(it)))
        }
        repositoryTasks.exception.addObserver {
            isVisibleTaskList.postValue(false)
            isVisibleTextNoItems.postValue(true)
            state.postValue(ErrorTaskListState(Response(null, it)))
        }
    }

    fun load() = launchSilent(coroutineContext, exceptionHandler, job) {
        state.postValue(LoadingTaskListState())
        repositoryTasks.init()
    }
}