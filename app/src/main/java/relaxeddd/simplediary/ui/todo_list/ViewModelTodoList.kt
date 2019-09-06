package relaxeddd.simplediary.ui.todo_list

import androidx.lifecycle.MutableLiveData
import relaxeddd.simplediary.App
import relaxeddd.simplediary.model.repository.RepositoryPreferences
import relaxeddd.simplediary.model.repository.RepositoryTasks
import relaxeddd.simplediary.ui.ViewModelBase

class ViewModelTodoList(app: App, repositoryTasks: RepositoryTasks, preferences: RepositoryPreferences)
    : ViewModelBase(app, preferences) {

    val isVisibleList = MutableLiveData(false)
    val isVisibleTextNoItems = MutableLiveData(true)
    val listTasks = repositoryTasks.tasks
}