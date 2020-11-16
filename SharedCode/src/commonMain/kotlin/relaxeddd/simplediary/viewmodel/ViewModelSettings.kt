package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.utils.ERROR_TEXT

class ViewModelSettings : ViewModelBase() {

    private val repositoryUsers = repoUsers

    fun onClickedLogout() {
        isVisibleProgressBarM.value = true
        repositoryUsers.signOut { isSuccess ->
            if (isSuccess) {
                isVisibleProgressBarM.value = false
                actionM.postValue(Action(EventType.GO_SCREEN_LOGIN))
            } else {
                actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, "Logout error")))) //TODO translation
            }
        }
    }
}
