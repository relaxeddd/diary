package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.push.MyFirebaseMessagingService

class RepositoryInit(private val apiHelper: ApiHelper, private val preferences: RepositoryPreferences) {

    val liveDataInitResult = MutableLiveData<Result<InitContent>>(Result(RESULT_UNDEFINED))

    suspend fun init() : Result<InitContent> {
        val isSuccessFirebaseInit = initFirebase()
        val initResult = if (isSuccessFirebaseInit) initRequest() else Result(RESULT_ERROR_UNAUTHORIZED)

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

    private suspend fun initRequest() : Result<InitContent> {
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