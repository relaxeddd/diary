package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.push.MyFirebaseMessagingService

class RepositoryUsers(private val apiHelper: ApiHelper, private val repositoryCommon: RepositoryCommon,
                      private val repositoryTasks: RepositoryTasks, private val sharedHelper: SharedHelper) {

    var liveDataUser = MutableLiveData<User>(null)
    val liveDataIsActualVersion = MutableLiveData(true)

    fun isAuthorized() = FirebaseAuth.getInstance().currentUser != null

    //------------------------------------------------------------------------------------------------------------------
    suspend fun init(listener: ListenerResult<Boolean>? = null) {
        repositoryCommon.initFirebase { isSuccess ->
            CoroutineScope(Dispatchers.Main).launch {
                if (!isSuccess) {
                    listener?.onResult(false)
                    return@launch
                }

                val firebaseUser = repositoryCommon.firebaseUser
                val tokenId = repositoryCommon.tokenId
                var pushToken = MyFirebaseMessagingService.pushToken

                if (pushToken.isEmpty()) {
                    pushToken = sharedHelper.getPushToken()
                }
                if (pushToken.isEmpty()) {
                    @Suppress("DEPRECATION")
                    pushToken = FirebaseInstanceId.getInstance().token ?: ""
                }
                if (pushToken.isEmpty()) {
                    showToast(R.string.error_push_token)
                }
                if (firebaseUser == null) {
                    showToast(getErrorString(RESULT_ERROR_UNAUTHORIZED))
                    listener?.onResult(false)
                    return@launch
                }

                val answerInitData = apiHelper.requestInit(firebaseUser, tokenId, pushToken)

                if (answerInitData?.result != null && answerInitData.result.isSuccess() && answerInitData.user?.userId?.isNotEmpty() == true) {
                    liveDataUser.value = answerInitData.user
                    if (!answerInitData.isActualVersion) {
                        liveDataIsActualVersion.value = answerInitData.isActualVersion
                    }

                    if (answerInitData.tasks != null) {
                        withContext(Dispatchers.IO) {
                            repositoryTasks.updateTasks(answerInitData.tasks)
                        }
                    }

                    listener?.onResult(true)
                } else if (answerInitData?.result != null) {
                    showToast(getErrorString(answerInitData.result))
                    listener?.onResult(false)
                } else {
                    showToast(R.string.error_initialization)
                    listener?.onResult(false)
                }
            }
        }
    }

    suspend fun deleteUserInfo() {
        withContext(Dispatchers.Main) {
            liveDataUser.value = null
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun updateUser(user: User, oldUser: User?) : Boolean {
        liveDataUser.postValue(user)

        val firebaseUser = repositoryCommon.firebaseUser
        val tokenId = repositoryCommon.tokenId
        val updateResult = apiHelper.requestUpdateUser(firebaseUser, tokenId)

        return if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            showToast(getErrorString(updateResult.result))
            liveDataUser.postValue(oldUser)
            false
        } else if (updateResult != null && updateResult.result !== null) {
            true
        } else {
            showToast(R.string.error_update)
            false
        }
    }
}