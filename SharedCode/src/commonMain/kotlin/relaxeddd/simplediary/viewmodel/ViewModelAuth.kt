package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class ViewModelAuth : ViewModelBase() {

    private val repositoryUsers = repoUsers

    private val isRegistrationViewM = MutableLiveData(true)
    val isRegistrationView: LiveData<Boolean> = isRegistrationViewM

    private val isAuthorizedM = MutableLiveData(false)
    val isAuthorized = isAuthorizedM

    private val errorAuthM = MutableLiveData("")
    val errorAuth = errorAuthM

    private val observerIsAuthorized: (Boolean) -> Unit = {
        isAuthorizedM.value = it
    }

    private val observerErrorAuth: (String) -> Unit = {
        errorAuthM.value = it
    }

    override fun onFill() {
        super.onFill()
        repositoryUsers.isAuthorized.addObserver(observerIsAuthorized)
        repositoryUsers.errorAuth.addObserver(observerErrorAuth)
    }

    override fun onCleared() {
        super.onCleared()
        isRegistrationViewM.removeAllObservers()
        repositoryUsers.isAuthorized.removeObserver(observerIsAuthorized)
        repositoryUsers.errorAuth.removeObserver(observerErrorAuth)
    }

    fun onClickedNewAccountOrHaveAccount() {
        isRegistrationViewM.value = !isRegistrationView.value
    }

    fun onClickedLoginOrCreate(email: String, password: String) {
        if (isRegistrationView.value) {
            repositoryUsers.register(email, password)
        } else {
            repositoryUsers.signIn(email, password)
        }
    }
}
