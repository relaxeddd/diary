package relaxeddd.simplediary.di

import relaxeddd.simplediary.viewmodel.*
import relaxeddd.simplediary.viewmodel.ViewModelAuth
import relaxeddd.simplediary.viewmodel.ViewModelLogo
import relaxeddd.simplediary.viewmodel.ViewModelSettings
import relaxeddd.simplediary.viewmodel.ViewModelTaskCard
import relaxeddd.simplediary.viewmodel.ViewModelTaskListActual
import relaxeddd.simplediary.viewmodel.ViewModelTaskListArchive
import relaxeddd.simplediary.viewmodel.ViewModelTaskListPersistent

object ViewModelCreator {

    fun createLogoViewModel() : IViewModelLogo {
        return ViewModelLogo(repoUsers)
    }

    fun createAuthViewModel() : IViewModelAuth {
        return ViewModelAuth(repoUsers)
    }

    fun createSettingsViewModel() : IViewModelSettings {
        return ViewModelSettings(repoUsers, repoTasks)
    }

    fun createTaskCardViewModel() : IViewModelTaskCard {
        return ViewModelTaskCard(repoTasks)
    }

    fun createTaskListActualViewModel() : IViewModelTaskListActual {
        return ViewModelTaskListActual(repoTasks)
    }

    fun createTaskListPersistentViewModel() : IViewModelTaskListPersistent {
        return ViewModelTaskListPersistent(repoTasks)
    }

    fun createTaskListArchiveViewModel() : IViewModelTaskListArchive {
        return ViewModelTaskListArchive(repoTasks)
    }
}
