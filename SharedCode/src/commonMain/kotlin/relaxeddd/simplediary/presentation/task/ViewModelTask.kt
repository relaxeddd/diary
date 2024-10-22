package relaxeddd.simplediary.presentation.task

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.Task
import relaxeddd.simplediary.data.repository.RepositoryTasks
import relaxeddd.simplediary.presentation.base.ViewModelBase

internal abstract class ViewModelTask(private val repositoryTasks: RepositoryTasks) : ViewModelBase(), IViewModelTask {

    protected fun updateTask(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                             repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                             isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>,
                             onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        isVisibleProgressBarM.value = true
        repositoryTasks.updateTask(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                                   isPersistent, isCompleted, exDates, remindHours) {
            isVisibleProgressBarM.value = false
            onCompleted?.invoke(it)
        }
    }

    protected fun createTask(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                             repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                             isCompleted: Boolean, remindHours: List<Int>,
                             onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        isVisibleProgressBarM.value = true
        repositoryTasks.createTask(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                                   isPersistent, isCompleted, remindHours) {
            isVisibleProgressBarM.value = false
            onCompleted?.invoke(it)
        }
    }
}
