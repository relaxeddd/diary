package relaxeddd.simplediary.model.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.push.MyFirebaseMessagingService

class RepositoryCommon(private val apiHelper: ApiHelper) {

    var firebaseUser: FirebaseUser? = null
        private set
    var tokenId: String? = null
        private set

    fun initFirebase(initCallback: (isSuccess: Boolean) -> Unit) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            initCallback(false)
            return
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser

        apiHelper.initUserTokenId(firebaseUser) {
            if (it.isSuccess() && it.value != null) {
                apiHelper.initPushTokenId { pushTokenAnswer ->
                    if (pushTokenAnswer.isSuccess() && pushTokenAnswer.value != null) {
                        MyFirebaseMessagingService.pushToken = pushTokenAnswer.value
                    }
                    tokenId = it.value
                    initCallback(true)
                }
            } else {
                showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
                initCallback(false)
            }
        }
    }

    suspend fun sendFeedback(feedback: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
            return
        }
        if (feedback.isEmpty() || feedback.length < 6) {
            showToast(getErrorString(RESULT_ERROR_FEEDBACK_TOO_SHORT))
            return
        }

        val answer = apiHelper.requestSendFeedback(firebaseUser, tokenId, feedback)

        if (answer?.isSuccess() == true) {
            showToast(R.string.thank_you)
        } else {
            showToast(getErrorString(answer))
        }
    }
}