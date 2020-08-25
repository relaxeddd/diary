package relaxeddd.simplediary.utils.live_data

import relaxeddd.simplediary.postOnMainThread

abstract class LiveData<T> {

    protected val lock = Any() //TODO synchronization

    abstract val value: T

    protected val observers = ArrayList<(T) -> Unit>()

    fun addObserver(observer: (T) -> Unit) {
        postOnMainThread {
            observers.add(observer)
            observer.invoke(value)
        }
    }

    fun removeObserver(observer: (T) -> Unit) {
        postOnMainThread {
            observers.remove(observer)
        }
    }
}
