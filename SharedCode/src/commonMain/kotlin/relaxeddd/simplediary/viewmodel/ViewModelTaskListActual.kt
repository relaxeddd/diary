package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task

class ViewModelTaskListActual : ViewModelTaskList() {

    override fun filterRule(task: Task) = !task.isCompleted

    fun completeTask(id: Long) {
        tasks.value.find { it.id == id }?.let {
            if (!it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.priority, it.rrule, it.location, it.start, it.end, true)
            }
        }
    }
}
