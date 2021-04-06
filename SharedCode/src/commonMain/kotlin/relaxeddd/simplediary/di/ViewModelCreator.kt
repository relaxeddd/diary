package relaxeddd.simplediary.di

import relaxeddd.simplediary.presentation.auth.ViewModelAuth
import relaxeddd.simplediary.presentation.splash.ViewModelLogo
import relaxeddd.simplediary.presentation.settings.ViewModelSettings
import relaxeddd.simplediary.presentation.task.card.ViewModelTaskCard
import relaxeddd.simplediary.presentation.auth.IViewModelAuth
import relaxeddd.simplediary.presentation.settings.IViewModelSettings
import relaxeddd.simplediary.presentation.splash.IViewModelLogo
import relaxeddd.simplediary.presentation.task.card.IViewModelTaskCard
import relaxeddd.simplediary.presentation.task.list.*
import relaxeddd.simplediary.presentation.task.list.ViewModelTaskListActual
import relaxeddd.simplediary.presentation.task.list.ViewModelTaskListArchive
import relaxeddd.simplediary.presentation.task.list.ViewModelTaskListPersistent

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
