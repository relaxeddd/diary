package relaxeddd.simplediary.model.repository

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.http.ApiHelper

class RepositoryUsers(private val apiHelper: ApiHelper, repositoryInit: RepositoryInit) {

    val user = MutableLiveData<User>(null)

    //------------------------------------------------------------------------------------------------------------------
    init {
        repositoryInit.liveDataInitResult.observeForever {
            if (it.isSuccess && it.content != null && it.content.user?.userId?.isNotEmpty() == true) {
                user.value = it.content.user
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
    private suspend fun updateUser(user: User, oldUser: User?) : ServerAnswer<UserContent> {
        this.user.postValue(user)

        val updateResult = apiHelper.requestUpdateUser()

        if (updateResult.isFailure) {
            this.user.postValue(oldUser)
        }

        return updateResult
    }
}