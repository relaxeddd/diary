package relaxeddd.simplediary.viewmodel

import org.kodein.di.erased.instance
import relaxeddd.simplediary.di.KodeinInjector
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.source.repository.RepositoryTasks

abstract class ViewModelTask : ViewModelBase() {

    protected val repositoryTasks by KodeinInjector.instance<RepositoryTasks>()

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
