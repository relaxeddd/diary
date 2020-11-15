package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.getSavedEmail
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

    private val textEmailM = MutableLiveData(getSavedEmail())
    val textEmail: LiveData<String> = textEmailM

    private val textPasswordM = MutableLiveData("")
    val textPassword: LiveData<String> = textPasswordM

    private val textRepeatPasswordM = MutableLiveData("")
    val textRepeatPassword: LiveData<String> = textRepeatPasswordM

    private val observerIsAuthorized: (Boolean) -> Unit = { isAuthorized ->
        isAuthorizedM.value = isAuthorized
        if (isAuthorized) {
            actionM.postValue(Action(EventType.GO_SCREEN_LIST))
        }
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

    fun onChangedEmail(value: String) {
        if (textEmailM.value != value) {
            textEmailM.postValue(value)
            errorAuthM.value = ""
        }
    }

    fun onChangedPassword(value: String) {
        if (textPasswordM.value != value) {
            textPasswordM.postValue(value)
            errorAuthM.value = ""
        }
    }

    fun onChangedRepeatPassword(value: String) {
        if (textRepeatPasswordM.value != value) {
            textRepeatPasswordM.postValue(value)
            errorAuthM.value = ""
        }
    }

    fun onClickedHaveNotAccount() {
        isRegistrationViewM.value = true
    }

    fun onClickedAlreadyHaveAccount() {
        isRegistrationViewM.value = false
    }

    fun onClickedLogin() {
        val email = textEmail.value
        val password = textPassword.value

        //TODO translations
        if (isRegistrationView.value) {
            errorAuthM.value = "Internal error"
            return
        }
        if (email.length < 4) {
            errorAuthM.value = "Email too short. Need at least 4 symbols."
            return
        }

        isVisibleProgressBarM.value = true
        repositoryUsers.signIn(email, password) {
            isVisibleProgressBarM.value = false
        }
    }

    fun onClickedCreate() {
        val email = textEmail.value
        val password = textPassword.value
        val repeatPassword = textRepeatPassword.value

        //TODO translations
        if (!isRegistrationView.value) {
            errorAuthM.value = "Internal error"
            return
        }
        if (email.length < 4) {
            errorAuthM.value = "Email too short. Need at least 4 symbols."
            return
        }
        if (password.length < 6) {
            errorAuthM.value = "Password too short. Need at least 6 symbols."
            return
        }
        if (password != repeatPassword) {
            errorAuthM.value = "Repeated password doesn't match"
            return
        }

        isVisibleProgressBarM.value = true
        repositoryUsers.register(email, password) {
            isVisibleProgressBarM.value = false
        }
    }
}
