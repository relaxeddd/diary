package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.source.repository.RepositoryTasks

internal class ViewModelTaskListPersistent(repositoryTasks: RepositoryTasks) : ViewModelTaskList(repositoryTasks), IViewModelTaskListPersistent {

    override val isAddCurrentDayTask = false
    override val isAddIntermediateDayTasks = false

    override fun filterRule(task: Task) = task.isPersistent

    override fun onClickedCompleteTask(id: String) {
        completeTask(id)
    }

    override fun onClickedRestoreTask(id: String) {
        restoreTask(id)
    }

    private fun completeTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (!it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.comment, it.location, it.priority, it.repeat, it.repeatCount,
                           it.start, it.end, it.untilDate, it.isPersistent, true, it.exDates,
                           it.remindHours)
            }
        }
    }

    private fun restoreTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.comment, it.location, it.priority, it.repeat, it.repeatCount,
                           it.start, it.end, it.untilDate, it.isPersistent, false, it.exDates,
                           it.remindHours)
            }
        }
    }

    override fun sortTasks(tasks: List<Task>) = tasks.sortedWith { task1, task2 ->
        if (task1.isCompleted && !task2.isCompleted) 1 else if (!task1.isCompleted && task2.isCompleted) -1 else task2.priority.compareTo(
            task1.priority
        )
    }
}
