package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers

class ViewModelLogo : ViewModelBase() {

    private val repositoryUsers = repoUsers

    fun checkAuthorized() : Boolean {
        return repositoryUsers.checkAuthorized()
    }
}
