package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.TaskModel
import relaxeddd.simplediary.TaskModelQueries

internal class DaoTask(private val taskModelQueries: TaskModelQueries) {

    internal fun select() : List<TaskModel> = taskModelQueries.selectAll().executeAsList()

    internal fun create(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                        repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                        isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>) : Long {
        taskModelQueries.insertItem(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                      isPersistent, isCompleted, exDates, remindHours)
        return taskModelQueries.lastInsertRowId().executeAsOne()
    }

    internal fun update(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                        repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                        isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>) {
        taskModelQueries.updateItem(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                      isPersistent, isCompleted, exDates, remindHours)
    }

    internal fun delete(id: String) {
        taskModelQueries.deleteItem(id)
    }
}
