package relaxeddd.simplediary.ui

import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import relaxeddd.simplediary.App
import relaxeddd.simplediary.common.NavigationEvent

open class ViewModelBase(app: App) : AndroidViewModel(app) {

    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)
    val navigateEvent = MutableLiveData<NavigationEvent>()

    open fun onViewResume() {}

    protected fun getString(@StringRes resId: Int, arg1: Any? = null, arg2: Any? = null) : String {
        return getApplication<App>().getString(resId, arg1, arg2)
    }
}