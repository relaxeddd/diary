package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.data.TaskModel

class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun create(title: String, desc: String) : Long {
        db.insertItem(title, desc)
        return db.lastInsertRowId().executeAsOne()
    }

    internal fun delete(id: Long) {
        db.deleteItem(id)
    }

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()
}