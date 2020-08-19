package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoTasks
import relaxeddd.simplediary.domain.Response

abstract class ViewModelTask : ViewModelBase() {

    protected val repositoryTasks = repoTasks

    protected fun updateTask(id: Long, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                           start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<Unit>) -> Unit)? = null) {
        performTaskOperation {
            repositoryTasks.updateTask(id, title, desc, priority, rrule, location, start, end, isCompleted, onCompleted)
        }
    }

    protected fun createTask(title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                           start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<Unit>) -> Unit)? = null) {
        performTaskOperation {
            repositoryTasks.createTask(title, desc, priority, rrule, location, start, end, isCompleted, onCompleted)
        }
    }

    private fun performTaskOperation(operation: suspend () -> Unit) {
        operationWithLoading {
            operation()
        }
    }
}
