package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.data.TaskModel

class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()

    internal fun create(title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long,
                        end: Long) : Long {
        db.insertItem(title, desc, priority, rrule, location, start, end)
        return db.lastInsertRowId().executeAsOne()
    }

    internal fun update(id: Long, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                        start: Long, end: Long) {
        db.updateItem(id, title, desc, priority, rrule, location, start, end)
    }

    internal fun delete(id: Long) {
        db.deleteItem(id)
    }
}
