package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task

class ViewModelTaskListActual : ViewModelTaskList() {

    override fun filterRule(task: Task) = !task.isCompleted

    fun completeTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (!it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.comment, it.location, it.priority, it.repeat, it.repeatCount,
                           it.start, it.end, it.untilDate, it.isPersistent, true, it.exDates,
                           it.remindHours)
            }
        }
    }
}
