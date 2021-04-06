package relaxeddd.simplediary.utils.observable

import relaxeddd.simplediary.postOnMainThread

internal class MutableObservable<T>(value: T) : Observable<T>() {

    override var value: T = value
        set(value) {
            if (value != field) {
                field = value
                notifyValueChanged()
            }
        }

    fun postValue(value: T) {
        postOnMainThread {
            this.value = value
        }
    }

    fun removeAllObservers() {
        observers.clear()
    }

    private fun notifyValueChanged() {
        observers.forEach { it(value) }
    }
}
