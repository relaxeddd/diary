package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.usecase.task.UseCaseTaskGetList
import relaxeddd.simplediary.utils.launchSilent
import kotlin.coroutines.CoroutineContext

class ViewModelTaskList : ViewModelBase() {

    val listTasks = MutableLiveData<TaskListState>(NotLoadedTaskListState())
    val isVisibleTextNoItems = MutableLiveData(true)
    val isVisibleTaskList = MutableLiveData(true)

    private val useCaseTaskGetList by KodeinInjector.instance<UseCaseTaskGetList>()

    private val coroutineContext by KodeinInjector.instance<CoroutineContext>()
    private var job: Job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        print(e)
    }

    fun loadTasks() = launchSilent(coroutineContext, exceptionHandler, job) {
        listTasks.postValue(LoadingTaskListState())

        val response = useCaseTaskGetList.run()

        if (response.isValid) {
            listTasks.postValue(SuccessTaskListState(response))
            isVisibleTextNoItems.postValue(response.data?.isNotEmpty() == true)
        } else {
            listTasks.postValue(ErrorTaskListState(response))
            isVisibleTextNoItems.postValue(response.data?.isNotEmpty() == true)
        }
    }
}