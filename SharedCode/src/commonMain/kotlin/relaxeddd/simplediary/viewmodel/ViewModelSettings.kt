package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.source.repository.RepositoryUsers
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TEXT

internal class ViewModelSettings(private val repositoryUsers: RepositoryUsers,
                                 private val repositoryTasks: RepositoryTasks) : ViewModelBase(), IViewModelSettings {

    override fun onClickedSaveTasks() {
        actionM.postValue(Action(EventType.NAVIGATION_DIALOG_CONFIRM_TASKS_SAVE))
    }

    override fun onClickedLoadTasks() {
        actionM.postValue(Action(EventType.NAVIGATION_DIALOG_CONFIRM_TASKS_LOAD))
    }

    override fun onConfirmedSave() {
        isVisibleProgressBarM.value = true
        repositoryTasks.requestSaveTasks { result ->
            isVisibleProgressBarM.value = false
            //TODO translation
            val message = if (result?.isSuccess() == true) "Success save" else "Error save: ${result?.message ?: "unknown"}"
            actionM.postValue(Action(EventType.ALERT, mapOf(Pair(TEXT, message))))
        }
    }

    override fun onConfirmedLoad() {
        isVisibleProgressBarM.value = true
        repositoryTasks.requestLoadTasks { result ->
            isVisibleProgressBarM.value = false
            //TODO translation
            val message = if (result?.isSuccess() == true) "Success load" else "Error load: ${result?.message ?: "unknown"}"
            actionM.postValue(Action(EventType.ALERT, mapOf(Pair(TEXT, message))))
        }
    }

    override fun onClickedLogout() {
        isVisibleProgressBarM.value = true
        repositoryUsers.signOut { isSuccess ->
            isVisibleProgressBarM.value = false
            if (isSuccess) {
                actionM.postValue(Action(EventType.GO_SCREEN_LOGIN))
            } else {
                actionM.postValue(Action(EventType.ERROR, mapOf(Pair(ERROR_TEXT, "Logout error")))) //TODO translation
            }
        }
    }
}
