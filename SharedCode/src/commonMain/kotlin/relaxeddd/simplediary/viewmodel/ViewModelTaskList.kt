package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.usecase.task.UseCaseTaskGetList
import relaxeddd.simplediary.utils.launchSilent
import kotlin.coroutines.CoroutineContext

class ViewModelTaskList : ViewModel() {

    var tasksLiveData = MutableLiveData<TaskListState>(LoadingTaskListState())

    private val useCaseTaskGetList by KodeinInjector.instance<UseCaseTaskGetList>()

    private val coroutineContext by KodeinInjector.instance<CoroutineContext>()
    private var job: Job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        print(e)
    }

    fun loadTasks() = launchSilent(coroutineContext, exceptionHandler, job) {
        tasksLiveData.postValue(LoadingTaskListState())
        val response = useCaseTaskGetList.run()
        if (response is Response.Success) {
            tasksLiveData.postValue(SuccessTaskListState(response))
        } else if (response is Response.Error){
            tasksLiveData.postValue(ErrorTaskListState(response))
        }
    }
}