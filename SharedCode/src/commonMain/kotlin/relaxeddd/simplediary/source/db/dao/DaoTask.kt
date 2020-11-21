package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.TaskModel

class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()

    internal fun create(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                        repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                        isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>) : Long {
        db.insertItem(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                      isPersistent, isCompleted, exDates, remindHours)
        return db.lastInsertRowId().executeAsOne()
    }

    internal fun update(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                        repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                        isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>) {
        db.updateItem(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                      isPersistent, isCompleted, exDates, remindHours)
    }

    internal fun delete(id: String) {
        db.deleteItem(id)
    }
}
