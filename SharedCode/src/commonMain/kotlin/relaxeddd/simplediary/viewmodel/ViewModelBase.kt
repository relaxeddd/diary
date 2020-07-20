package relaxeddd.simplediary.viewmodel

import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.model.NavigationEvent
import kotlin.coroutines.CoroutineContext

open class ViewModelBase : ViewModel() {

    protected val coroutineContext by KodeinInjector.instance<CoroutineContext>()
    protected var job: Job = Job()
    protected val exceptionHandler = CoroutineExceptionHandler { _, e -> print(e) }

    val navigateEvent = MutableLiveData<NavigationEvent?>(null)
}