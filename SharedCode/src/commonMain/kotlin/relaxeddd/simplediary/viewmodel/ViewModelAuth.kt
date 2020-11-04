package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class ViewModelAuth : ViewModelBase() {

    private val isRegistrationViewM = MutableLiveData(false)
    val isRegistrationView: LiveData<Boolean> = isRegistrationViewM

    override fun onFill() {
        super.onFill()

    }

    override fun onCleared() {
        super.onCleared()
        isRegistrationViewM.removeAllObservers()
    }
}