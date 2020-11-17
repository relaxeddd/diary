package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.domain.model.Task

class ViewModelTaskListArchive : ViewModelTaskList() {

    override fun filterRule(task: Task) = task.isCompleted

    fun restoreTask(id: String) {
        tasks.value.find { it.id == id }?.let {
            if (it.isCompleted) {
                updateTask(it.id, it.title, it.desc, it.priority, it.rrule, it.location, it.start, it.end, false)
            }
        }
    }
}
