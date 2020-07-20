package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task

sealed class TaskListState {
    abstract val response: Response<List<Task>>?
}
data class SuccessTaskListState(override val response: Response<List<Task>>) : TaskListState()
data class LoadingTaskListState(override val response: Response<List<Task>>? = null) : TaskListState()
data class ErrorTaskListState(override val response: Response<List<Task>>) : TaskListState()
data class NotLoadedTaskListState(override val response: Response<List<Task>>? = null) : TaskListState()

sealed class TaskCreateState {
    abstract val response: Response<Unit>?
}
data class SuccessTaskCreateState(override val response: Response<Unit>) : TaskCreateState()
data class LoadingTaskCreateState(override val response: Response<Unit>? = null) : TaskCreateState()
data class ErrorTaskCreateState(override val response: Response<Unit>) : TaskCreateState()
data class NothingTaskCreateState(override val response: Response<Unit>? = null) : TaskCreateState()