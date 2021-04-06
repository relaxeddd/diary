package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.source.repository.RepositoryUsers

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
