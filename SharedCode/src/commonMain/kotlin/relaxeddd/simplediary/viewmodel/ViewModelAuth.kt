package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.getSavedEmail
import relaxeddd.simplediary.source.repository.RepositoryUsers
import relaxeddd.simplediary.utils.observable.Observable
import relaxeddd.simplediary.utils.observable.MutableObservable

internal class ViewModelAuth(private val repositoryUsers : RepositoryUsers) : ViewModelBase(), IViewModelAuth {

    private val isRegistrationViewM = MutableObservable(true)
    override val isRegistrationView: Observable<Boolean> = isRegistrationViewM

    private val isAuthorizedM = MutableObservable(false)
    override val isAuthorized: Observable<Boolean> get() = isAuthorizedM

    private val errorAuthM = MutableObservable("")
    override val errorAuth: Observable<String> get() = errorAuthM

    private val textEmailM = MutableObservable(getSavedEmail())
    override val textEmail: Observable<String> = textEmailM

    private val textPasswordM = MutableObservable("")
    override val textPassword: Observable<String> = textPasswordM

    private val textRepeatPasswordM = MutableObservable("")
    override val textRepeatPassword: Observable<String> = textRepeatPasswordM

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
        isAuthorizedM.removeAllObservers()
        errorAuthM.removeAllObservers()
        textEmailM.removeAllObservers()
        textPasswordM.removeAllObservers()
        textRepeatPasswordM.removeAllObservers()
        repositoryUsers.isAuthorized.removeObserver(observerIsAuthorized)
        repositoryUsers.errorAuth.removeObserver(observerErrorAuth)
    }

    override fun onChangedEmail(value: String) {
        if (textEmailM.value != value) {
            textEmailM.postValue(value)
            errorAuthM.value = ""
        }
    }

    override fun onChangedPassword(value: String) {
        if (textPasswordM.value != value) {
            textPasswordM.postValue(value)
            errorAuthM.value = ""
        }
    }

    override fun onChangedRepeatPassword(value: String) {
        if (textRepeatPasswordM.value != value) {
            textRepeatPasswordM.postValue(value)
            errorAuthM.value = ""
        }
    }

    override fun onClickedHaveNotAccount() {
        isRegistrationViewM.value = true
    }

    override fun onClickedAlreadyHaveAccount() {
        isRegistrationViewM.value = false
    }

    override fun onClickedLogin() {
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

    override fun onClickedCreate() {
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
