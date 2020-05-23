package relaxeddd.simplediary.domain.usecase.task

import relaxeddd.simplediary.data.TaskModel
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.usecase.base.BaseUseCase
import relaxeddd.simplediary.source.repository.RepoTask

class UseCaseTaskGetList(val repository: RepoTask) : BaseUseCase<Nothing, List<TaskModel>>() {

    override suspend fun run(): Response<List<TaskModel>> {
        return repository.getTaskList()
    }
}