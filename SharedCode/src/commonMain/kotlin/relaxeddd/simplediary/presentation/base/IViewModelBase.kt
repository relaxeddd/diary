package relaxeddd.simplediary.presentation.base

import relaxeddd.simplediary.domain.Action
import relaxeddd.simplediary.domain.observable.Observable

interface IViewModelBase {

    val action: Observable<Action?>
    val isVisibleProgressBar: Observable<Boolean>

    fun onFill()
    fun onCleared()
}
