package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.push.MyFirebaseMessagingService

class RepositoryInit(private val apiHelper: ApiHelper, private val preferences: RepositoryPreferences) {

    val liveDataInitResult = MutableLiveData<ServerAnswer<InitContent>>(ServerAnswer(RESULT_UNDEFINED))

    suspend fun init() : ServerAnswer<InitContent> {
        val isSuccessFirebaseInit = initFirebase()
        val initResult = if (isSuccessFirebaseInit) initRequest() else ServerAnswer(RESULT_ERROR_UNAUTHORIZED)

        liveDataInitResult.postValue(initResult)
        return initResult
    }

    private fun initFirebase() : Boolean {
        val answerInitUserTokenId = apiHelper.initUserTokenId()

        if (answerInitUserTokenId.isSuccess()) {
            val answerInitPushTokenId = apiHelper.initPushTokenId()

            if (answerInitPushTokenId.isSuccess() && answerInitPushTokenId.value != null) {
                MyFirebaseMessagingService.pushToken = answerInitPushTokenId.value
            }
        }

        return answerInitUserTokenId.isSuccess()
    }

    private suspend fun initRequest() : ServerAnswer<InitContent> {
        var pushToken = MyFirebaseMessagingService.pushToken

        if (pushToken.isEmpty()) {
            pushToken = preferences.pushToken
        }
        if (pushToken.isEmpty()) {
            @Suppress("DEPRECATION")
            pushToken = FirebaseInstanceId.getInstance().token ?: ""
        }

        return apiHelper.requestInit(pushToken)
    }
}