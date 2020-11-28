package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task

class ViewModelTaskListPersistent : ViewModelTaskList() {

    override val isAddCurrentDayTask = false

    override fun filterRule(task: Task) = task.isPersistent

    fun onClickedCompleteTask(id: String) {
        completeTask(id)
    }

    fun onClickedRestoreTask(id: String) {
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

    fun restoreTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.comment, it.location, it.priority, it.repeat, it.repeatCount,
                           it.start, it.end, it.untilDate, it.isPersistent, false, it.exDates,
                           it.remindHours)
            }
        }
    }

    override fun sortTasks(tasks: List<Task>) = tasks.sortedWith(object: Comparator<Task> {
        override fun compare(task1: Task, task2: Task): Int {
            return if (task1.isCompleted && !task2.isCompleted) 1 else if (!task1.isCompleted && task2.isCompleted) -1 else task2.priority.compareTo(task1.priority)
        }
    })
}
