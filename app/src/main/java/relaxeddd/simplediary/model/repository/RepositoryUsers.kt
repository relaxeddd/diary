package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper

class RepositoryUsers(private val apiHelper: ApiHelper, repositoryInit: RepositoryInit) {

    val user = MutableLiveData<User>(null)

    //------------------------------------------------------------------------------------------------------------------
    init {
        repositoryInit.liveDataInitResult.observeForever {
            if (it?.result != null && it.result.isSuccess() && it.user?.userId?.isNotEmpty() == true) {
                user.value = it.user
            } else {
                user.value = null
            }
        }
    }

    suspend fun deleteUserInfo() {
        withContext(Dispatchers.Main) {
            user.value = null
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private suspend fun updateUser(user: User, oldUser: User?) : Boolean {
        this.user.postValue(user)

        val updateResult = apiHelper.requestUpdateUser()

        return if (updateResult != null && updateResult.result !== null && !updateResult.result.isSuccess()) {
            showToast(getErrorString(updateResult.result))
            this.user.postValue(oldUser)
            false
        } else if (updateResult != null && updateResult.result !== null) {
            true
        } else {
            showToast(R.string.error_update)
            false
        }
    }
}