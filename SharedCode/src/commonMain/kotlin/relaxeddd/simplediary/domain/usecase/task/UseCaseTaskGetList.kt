package relaxeddd.simplediary.domain.usecase.task

import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.domain.usecase.base.BaseUseCase
import relaxeddd.simplediary.source.repository.RepositoryTasks

class UseCaseTaskGetList(val repository: RepositoryTasks) : BaseUseCase<Nothing, List<Task>>() {

    override suspend fun run(): Response<List<Task>> {
        return repository.getTasks()
    }
}