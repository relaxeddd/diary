package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType

class ViewModelLogo : ViewModelBase() {

    private val repositoryUsers = repoUsers

    fun checkAuthorized() {
        repositoryUsers.checkAuthorized {
            val isAuthorized = repositoryUsers.isAuthorized.value
            if (isAuthorized) {
                actionM.postValue(Action(EventType.GO_SCREEN_LIST))
            } else {
                actionM.postValue(Action(EventType.GO_SCREEN_LOGIN))
            }
        }
    }
}
