package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.TaskModel

class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()

    internal fun create(id: String, title: String, desc: String?, priority: Int, repeat: Int, location: String?, start: Long,
                        end: Long, isCompleted: Boolean) : Long {
        db.insertItem(id, title, desc, priority, repeat, location, start, end, isCompleted)
        return db.lastInsertRowId().executeAsOne()
    }

    internal fun update(id: String, title: String, desc: String?, priority: Int, repeat: Int, location: String?,
                        start: Long, end: Long, isCompleted: Boolean) {
        db.updateItem(id, title, desc, priority, repeat, location, start, end, isCompleted)
    }

    internal fun delete(id: String) {
        db.deleteItem(id)
    }
}
