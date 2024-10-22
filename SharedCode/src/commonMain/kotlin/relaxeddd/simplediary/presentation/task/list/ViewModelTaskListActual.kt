package relaxeddd.simplediary.presentation.task.list

import relaxeddd.simplediary.domain.Action
import relaxeddd.simplediary.domain.EventType
import relaxeddd.simplediary.domain.RepeatRule
import relaxeddd.simplediary.domain.Task
import relaxeddd.simplediary.generateId
import relaxeddd.simplediary.data.repository.RepositoryTasks
import relaxeddd.simplediary.domain.ID

internal class ViewModelTaskListActual(repositoryTasks: RepositoryTasks) : ViewModelTaskList(repositoryTasks),
    IViewModelTaskListActual {

    override fun filterRule(task: Task) = !task.isCompleted && !task.isPersistent

    override fun onClickedCompleteTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (it.isRepetitive()) {
                actionM.postValue(Action(EventType.NAVIGATION_DIALOG_REPETITIVE_TASK_COMPLETE, mapOf(Pair(ID, id))))
            } else {
                completeTask(id)
            }
        }
    }

    override fun onClickedCompleteChildTask(id: String) {
        tasks.value.find { it.id == id }?.let { childTask ->
            if (!childTask.isCompleted) {
                val parentId = if (childTask.parentId.isNotBlank()) childTask.parentId else childTask.id
                val parentTask = if (id == parentId) childTask else tasks.value.find { it.id == parentId }

                createTask(generateId(), childTask.title, childTask.desc, childTask.comment, childTask.location,
                           childTask.priority, RepeatRule.NO.ordinal, 0, childTask.start, childTask.end,
                           0, false, true, childTask.remindHours)

                if (parentTask != null) {
                    val exDates = ArrayList(parentTask.exDates).apply { add(childTask.start) }
                    updateTask(parentTask.id, parentTask.title, parentTask.desc, parentTask.comment,
                               parentTask.location, parentTask.priority, parentTask.repeat, parentTask.repeatCount,
                               parentTask.start, parentTask.end, parentTask.untilDate, parentTask.isPersistent,
                               false, exDates, parentTask.remindHours)
                }
            }
        }
    }

    override fun onClickedCompleteParentTask(id: String) {
        val task = tasks.value.find { it.id == id }
        val parentId = if (task?.parentId?.isNotBlank() == true) task.parentId else task?.id

        completeTask(parentId ?: return)
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
}
