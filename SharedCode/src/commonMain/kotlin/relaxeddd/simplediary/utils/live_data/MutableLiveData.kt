package relaxeddd.simplediary.utils.live_data

import relaxeddd.simplediary.postOnMainThread

class MutableLiveData<T>(value: T) : LiveData<T>() {

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

    private fun notifyValueChanged() {
        observers.forEach { it(value) }
    }
}
