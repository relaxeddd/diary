package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.source.repository.RepositoryTasks

internal class ViewModelTaskListArchive(repositoryTasks: RepositoryTasks) : ViewModelTaskList(repositoryTasks), IViewModelTaskListArchive {

    override fun filterRule(task: Task) = task.isCompleted && !task.isPersistent

    override fun restoreTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.comment, it.location, it.priority, it.repeat, it.repeatCount,
                           it.start, it.end, it.untilDate, it.isPersistent, false, it.exDates,
                           it.remindHours)
            }
        }
    }
}
