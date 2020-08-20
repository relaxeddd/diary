package relaxeddd.simplediary.viewmodel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import relaxeddd.simplediary.di.coroutineCtx
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.launchSilent
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

open class ViewModelBase {

    private val coroutineContext = coroutineCtx
    private val job: Job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> print(e) }

    protected val actionM = MutableLiveData<Action?>(null)
    val action: LiveData<Action?> = actionM

    private val isVisibleProgressBarM = MutableLiveData(false)
    val isVisibleProgressBar: LiveData<Boolean> = isVisibleProgressBarM

    open fun onCleared() {
        //TODO
    }

    protected fun operationWithLoading(operation: suspend () -> Unit) {
        launchSilent(coroutineContext, exceptionHandler, job) {
            isVisibleProgressBarM.postValue(true)
            operation()
            isVisibleProgressBarM.postValue(false)
        }
    }
}
