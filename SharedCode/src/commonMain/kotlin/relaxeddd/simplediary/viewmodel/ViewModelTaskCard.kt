package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.utils.launchSilent

class ViewModelTaskCard : ViewModelBase() {

    private val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()

    val state = MutableLiveData<TaskCreateState>(EmptyTaskCreateState())

    fun createTask(title: String, desc: String? = null, priority: Int = 0, rrule: String? = null,
                   location: String? = null) = launchSilent(coroutineContext, exceptionHandler, job) {
        state.postValue(LoadingTaskCreateState())

        val response = repositoryTasks.createTask(title, desc, priority, rrule, location)

        if (response.isValid) {
            state.postValue(SuccessTaskCardState(response))
        } else {
            state.postValue(ErrorTaskCardState(response))
        }
    }

    fun updateTask(id: Long, title: String, desc: String? = null, priority: Int = 0, rrule: String? = null,
                   location: String? = null) = launchSilent(coroutineContext, exceptionHandler, job) {
        state.postValue(LoadingTaskCreateState())

        val response = repositoryTasks.updateTask(id, title, desc, priority, rrule, location)

        if (response.isValid) {
            state.postValue(SuccessTaskCardState(response))
        } else {
            state.postValue(ErrorTaskCardState(response))
        }
    }
}