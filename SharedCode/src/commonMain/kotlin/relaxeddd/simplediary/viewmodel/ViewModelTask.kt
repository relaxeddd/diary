package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoTasks
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

abstract class ViewModelTask : ViewModelBase() {

    protected val repositoryTasks = repoTasks

    protected fun updateTask(id: String, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                             start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        isVisibleProgressBarM.value = true
        repositoryTasks.updateTask(id, title, desc, priority, rrule, location, start, end, isCompleted) {
            isVisibleProgressBarM.value = false
            onCompleted?.invoke(it)
        }
    }

    protected fun createTask(id: String, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                             start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        isVisibleProgressBarM.value = true
        repositoryTasks.createTask(id, title, desc, priority, rrule, location, start, end, isCompleted) {
            isVisibleProgressBarM.value = false
            onCompleted?.invoke(it)
        }
    }
}
