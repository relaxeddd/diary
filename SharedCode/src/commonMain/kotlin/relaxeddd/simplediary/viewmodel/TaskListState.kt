package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.data.TaskModel
import relaxeddd.simplediary.domain.Response

sealed class TaskListState {
    abstract val response: Response<List<TaskModel>>?
}
data class SuccessTaskListState(override val response: Response<List<TaskModel>>) : TaskListState()
data class LoadingTaskListState(override val response: Response<List<TaskModel>>? = null) : TaskListState()
data class ErrorTaskListState(override val response: Response<List<TaskModel>>) : TaskListState()