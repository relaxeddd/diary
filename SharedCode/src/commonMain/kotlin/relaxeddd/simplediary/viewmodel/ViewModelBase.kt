package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

open class ViewModelBase {

    protected val actionM = MutableLiveData<Action?>(null)
    val action: LiveData<Action?> = actionM

    protected val isVisibleProgressBarM = MutableLiveData(false)
    val isVisibleProgressBar: LiveData<Boolean> = isVisibleProgressBarM

    open fun onCleared() {
        //TODO
    }

    protected fun operationWithLoading(operation: () -> Unit): () -> Unit = {
        isVisibleProgressBarM.postValue(true)
        operation()
        isVisibleProgressBarM.postValue(false)
    }
}
