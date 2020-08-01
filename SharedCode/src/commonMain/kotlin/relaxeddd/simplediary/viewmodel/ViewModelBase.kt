package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.model.Action
import kotlin.coroutines.CoroutineContext

open class ViewModelBase : ViewModel() {

    protected val coroutineContext by KodeinInjector.instance<CoroutineContext>()
    protected var job: Job = Job()
    protected val exceptionHandler = CoroutineExceptionHandler { _, e -> print(e) }

    protected val actionM = MutableLiveData<Action?>(null)
    val action: LiveData<Action?> = actionM

    protected val isVisibleProgressBarM = MutableLiveData(false)
    val isVisibleProgressBar: LiveData<Boolean> = isVisibleProgressBarM

    protected suspend fun operationWithLoading(operation: suspend () -> Unit) {
        isVisibleProgressBarM.postValue(true)
        operation()
        isVisibleProgressBarM.postValue(false)
    }
}