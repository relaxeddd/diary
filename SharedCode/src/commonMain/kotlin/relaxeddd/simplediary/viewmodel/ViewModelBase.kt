package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import relaxeddd.simplediary.di.coroutineCtx
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.launchSilent

open class ViewModelBase : ViewModel() {

    private val coroutineContext = coroutineCtx
    private val job: Job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, e -> print(e) }

    protected val actionM = MutableLiveData<Action?>(null)
    val action: LiveData<Action?> = actionM

    private val isVisibleProgressBarM = MutableLiveData(false)
    val isVisibleProgressBar: LiveData<Boolean> = isVisibleProgressBarM

    protected fun operationWithLoading(operation: suspend () -> Unit) {
        launchSilent(coroutineContext, exceptionHandler, job) {
            isVisibleProgressBarM.postValue(true)
            operation()
            isVisibleProgressBarM.postValue(false)
        }
    }
}
