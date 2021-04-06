package relaxeddd.simplediary.presentation.splash

import relaxeddd.simplediary.domain.Action
import relaxeddd.simplediary.domain.EventType
import relaxeddd.simplediary.data.repository.RepositoryUsers
import relaxeddd.simplediary.presentation.base.ViewModelBase

internal class ViewModelLogo(private val repositoryUsers: RepositoryUsers) : ViewModelBase(), IViewModelLogo {

    override fun checkAuthorized() {
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
