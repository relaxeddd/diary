package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

open class ViewModelBase {

    protected val actionM = MutableLiveData<Action?>(null)
    val action: LiveData<Action?> = actionM

    protected val isVisibleProgressBarM = MutableLiveData(false)
    val isVisibleProgressBar: LiveData<Boolean> = isVisibleProgressBarM

    open fun onFill() {}

    open fun onCleared() {
        actionM.removeAllObservers()
        isVisibleProgressBarM.removeAllObservers()
    }
}
