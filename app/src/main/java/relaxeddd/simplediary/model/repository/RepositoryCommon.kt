package relaxeddd.simplediary.model.repository

import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper

class RepositoryCommon(private val apiHelper: ApiHelper) {

    suspend fun sendFeedback(feedback: String) {
        if (feedback.isEmpty() || feedback.length < 6) {
            showToast(getErrorString(RESULT_ERROR_FEEDBACK_TOO_SHORT))
            return
        }

        val answer = apiHelper.requestSendFeedback(feedback)

        if (answer?.isSuccess() == true) {
            showToast(R.string.thank_you)
        } else {
            showToast(getErrorString(answer))
        }
    }
}