package relaxeddd.simplediary.model.repository

import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper

class RepositoryCommon(private val apiHelper: ApiHelper) {

    companion object {
        private const val MIN_FEEDBACK_LENGTH = 5
    }

    suspend fun sendFeedback(feedback: String) : ServerAnswer<Void> {
        if (feedback.isEmpty() || feedback.length <= MIN_FEEDBACK_LENGTH) {
            return ServerAnswer(RESULT_ERROR_FEEDBACK_TOO_SHORT)
        }

        return apiHelper.requestSendFeedback(feedback)
    }
}