package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.async
import relaxeddd.simplediary.checkAuthorization
import relaxeddd.simplediary.createFirebaseUser
import relaxeddd.simplediary.di.apiHelper
import relaxeddd.simplediary.domain.model.ResultInit
import relaxeddd.simplediary.domain.model.User
import relaxeddd.simplediary.loginFirebaseUser
import relaxeddd.simplediary.logout
import relaxeddd.simplediary.setSavedEmail
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class RepositoryUsers {

    private var isInitialized = false
    private var isInitializing = false

    private val userM = MutableLiveData<User?>(null)
    val user: LiveData<User?> = userM

    private val tokenIdM = MutableLiveData("")
    val tokenId: LiveData<String> = tokenIdM

    private val uidM = MutableLiveData("")
    val uid: LiveData<String> = uidM

    private val emailM = MutableLiveData("")
    val email: LiveData<String> = emailM

    private val errorAuthM = MutableLiveData("")
    val errorAuth: LiveData<String> = errorAuthM

    private val isAuthorizedM = MutableLiveData(false)
    val isAuthorized: LiveData<Boolean> = isAuthorizedM

    fun checkAuthorized(onFinish: () -> Unit) {
        checkAuthorization { tokenId, uid, email ->
            if (tokenId.isBlank()) {
                handleAuthResult(null, "", "", email, "")
                onFinish()
            } else {
                init(tokenId, uid, email, "", onFinish)
            }
        }
    }

    fun signIn(email: String, password: String, onFinish: () -> Unit) {
        loginFirebaseUser(email, password) { tokenId, uid, email, errorCode, errorDescription ->
            if (tokenId.isBlank()) {
                handleAuthResult(null, tokenId, uid, email, errorDescription ?: "")
                onFinish()
            } else {
                init(tokenId, uid, email, "", onFinish)
            }
        }
    }

    fun register(email: String, password: String, onFinish: () -> Unit) {
        createFirebaseUser(email, password) { tokenId, uid, email, errorCode, errorDescription ->
            if (tokenId.isBlank()) {
                handleAuthResult(null, tokenId, uid, email, errorDescription ?: "")
                onFinish()
            } else {
                init(tokenId, uid, email, "", onFinish)
            }
        }
    }

    fun signOut(listener: (isSuccess: Boolean) -> Unit) {
        logout { isSuccess ->
            if (isSuccess) {
                handleAuthResult(null, "", "", "")
            }
            listener(isSuccess)
        }
    }

    private fun init(tokenId: String, uid: String, email: String, pushToken: String, onFinish: (() -> Unit)? = null) {
        if (isInitializing) {
            return
        }
        if (isAuthorized.value) {
            print("\nAlready authorized\n")
            return
        }

        isInitializing = true
        async({
            apiHelper.requestInit(tokenId, uid, email, pushToken)
              }, { resultInit: ResultInit?, e: Exception? ->
            isInitializing = false

            val errorText = if (resultInit?.result?.isSuccess() == false) {
                if (resultInit.result.message.isNotBlank()) resultInit.result.message else "Unknown error"
            } else {
                ""
            }

            handleAuthResult(resultInit?.user, tokenId, uid, email, "", e, errorText)
            onFinish?.invoke()
        })
    }

    private fun handleAuthResult(user: User?, tokenId: String, uid: String, email: String,
                                 errorDescription: String = "", e: Exception? = null, networkErrorMessage: String = "") {
        if (email.isNotBlank()) {
            setSavedEmail(email)
        }

        userM.value = user
        tokenIdM.value = tokenId
        uidM.value = uid
        emailM.value = email
        isAuthorizedM.value = tokenId.isNotBlank() && user != null

        if (e != null) {
            errorAuthM.postValue(e.toString())
            print("\nReceived error $e\n")
        } else if (errorDescription.isNotBlank()) {
            errorAuthM.postValue(errorDescription)
            print("\nReceived error $errorDescription\n")
        } else if (networkErrorMessage.isNotBlank()) {
            errorAuthM.postValue(networkErrorMessage)
            print("\nReceived error $networkErrorMessage\n")
        }
    }
}
