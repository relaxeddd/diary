package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.createFirebaseUser
import relaxeddd.simplediary.isAuthorized
import relaxeddd.simplediary.loginFirebaseUser
import relaxeddd.simplediary.logout
import relaxeddd.simplediary.registerFirebaseUserListener
import relaxeddd.simplediary.setSavedEmail
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class RepositoryUsers {

    private val uidM = MutableLiveData("")
    val uid: LiveData<String> = uidM

    private val emailM = MutableLiveData("")
    val email: LiveData<String> = emailM

    private val errorAuthM = MutableLiveData("")
    val errorAuth: LiveData<String> = errorAuthM

    private val isAuthorizedM = MutableLiveData(false)
    val isAuthorized: LiveData<Boolean> = isAuthorizedM

    init {
        registerFirebaseUserListener { uid, email ->
            uidM.value = uid
            emailM.value = email
        }
    }

    fun checkAuthorized() : Boolean {
        val userAuthResult = isAuthorized()
        handleAuthResult(userAuthResult.first, userAuthResult.second, userAuthResult.third, "")
        return userAuthResult.first
    }

    fun signIn(email: String, password: String, onFinish: () -> Unit) {
        loginFirebaseUser(email, password) { uid, email, errorCode, errorDescription ->
            onFinish()
            handleAuthResult(errorCode == null, uid, email, errorDescription ?: "")
        }
    }

    fun register(email: String, password: String, onFinish: () -> Unit) {
        createFirebaseUser(email, password) { uid, email, errorCode, errorDescription ->
            onFinish()
            handleAuthResult(errorCode == null, uid, email, errorDescription ?: "")
        }
    }

    fun signOut(listener: (isSuccess: Boolean) -> Unit) {
        logout { isSuccess ->
            if (isSuccess) {
                isAuthorizedM.value = false
            }
            listener(isSuccess)
        }
    }

    private fun handleAuthResult(isAuthorized: Boolean, uid: String, email: String, errorDescription: String) {
        if (email.isNotBlank()) {
            setSavedEmail(email)
        }
        uidM.value = uid
        emailM.value = email
        errorAuthM.value = errorDescription
        isAuthorizedM.value = isAuthorized
    }
}
