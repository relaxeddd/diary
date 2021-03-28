package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.observable.Observable

interface IViewModelBase {

    val action: Observable<Action?>
    val isVisibleProgressBar: Observable<Boolean>

    fun onFill()
    fun onCleared()
}
