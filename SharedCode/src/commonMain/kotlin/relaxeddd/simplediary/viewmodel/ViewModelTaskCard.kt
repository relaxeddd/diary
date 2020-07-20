package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.launchSilent

class ViewModelTaskCard : ViewModelBase() {

    private val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()

    val state = MutableLiveData<TaskCreateState>(NothingTaskCreateState())

    fun createTask(title: String, desc: String) = launchSilent(coroutineContext, exceptionHandler, job) {
        state.postValue(LoadingTaskCreateState())

        val response = repositoryTasks.createTask(title, desc)

        if (response.isValid) {
            state.postValue(SuccessTaskCreateState(response))
        } else {
            state.postValue(ErrorTaskCreateState(response))
        }
    }
}