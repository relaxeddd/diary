package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoTasks
import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.model.Action
import relaxeddd.simplediary.domain.model.EventType
import relaxeddd.simplediary.utils.ERROR_TEXT
import relaxeddd.simplediary.utils.TEXT

class ViewModelSettings : ViewModelBase() {

    private val repositoryUsers = repoUsers
    private val repositoryTasks = repoTasks

    fun onClickedSaveTasks() {
        actionM.postValue(Action(EventType.NAVIGATION_DIALOG_CONFIRM_TASKS_SAVE))
    }

    fun onClickedLoadTasks() {
        actionM.postValue(Action(EventType.NAVIGATION_DIALOG_CONFIRM_TASKS_LOAD))
    }

    fun onConfirmedSave() {
        isVisibleProgressBarM.value = true
        repositoryTasks.requestSaveTasks { result ->
            isVisibleProgressBarM.value = false
            //TODO translation
            val message = if (result?.isSuccess() == true) "Success save" else "Error save: ${result?.message ?: "unknown"}"
            actionM.postValue(Action(EventType.ALERT, mapOf(Pair(TEXT, message))))
        }
    }

    fun onConfirmedLoad() {
        isVisibleProgressBarM.value = true
        repositoryTasks.requestLoadTasks { result ->
            isVisibleProgressBarM.value = false
            //TODO translation
            val message = if (result?.isSuccess() == true) "Success load" else "Error load: ${result?.message ?: "unknown"}"
            actionM.postValue(Action(EventType.ALERT, mapOf(Pair(TEXT, message))))
        }
    }

    fun onClickedLogout() {
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
