package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.iid.FirebaseInstanceId
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.push.MyFirebaseMessagingService

class RepositoryInit(private val apiHelper: ApiHelper, private val preferences: RepositoryPreferences) {

    val liveDataInitResult = MutableLiveData<InitResult>(null)

    suspend fun init() {
        var initResult: InitResult? = null
        val isSuccessFirebaseInit = initFirebase()

        if (isSuccessFirebaseInit) {
            initResult = initRequest()
        }

        liveDataInitResult.postValue(initResult)
    }

    private fun initFirebase() : Boolean {
        var isSuccess = false

        val answerInitUserTokenId = apiHelper.initUserTokenId()

        if (answerInitUserTokenId.isSuccess()) {
            val answerInitPushTokenId = apiHelper.initPushTokenId()

            if (answerInitPushTokenId.isSuccess() && answerInitPushTokenId.value != null) {
                MyFirebaseMessagingService.pushToken = answerInitPushTokenId.value
            }
            isSuccess = answerInitPushTokenId.isSuccess()
        } else {
            showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
        }

        return isSuccess
    }

    private suspend fun initRequest() : InitResult? {
        var pushToken = MyFirebaseMessagingService.pushToken

        if (pushToken.isEmpty()) {
            pushToken = preferences.pushToken
        }
        if (pushToken.isEmpty()) {
            @Suppress("DEPRECATION")
            pushToken = FirebaseInstanceId.getInstance().token ?: ""
        }
        if (pushToken.isEmpty()) {
            showToast(R.string.error_push_token)
        }

        return apiHelper.requestInit(pushToken)
    }
}