package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.utils.observable.Observable
import relaxeddd.simplediary.utils.observable.MutableObservable

internal abstract class ViewModelBase : IViewModelBase {

    protected val actionM = MutableObservable<Action?>(null)
    override val action: Observable<Action?> get() = actionM

    protected val isVisibleProgressBarM = MutableObservable(false)
    override val isVisibleProgressBar: Observable<Boolean> get() = isVisibleProgressBarM

    override fun onFill() {}

    override fun onCleared() {
        actionM.removeAllObservers()
        isVisibleProgressBarM.removeAllObservers()
    }
}
