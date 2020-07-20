package relaxeddd.simplediary.source.db.dao

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.data.TaskModel
import relaxeddd.simplediary.domain.model.Task

class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun create(title: String, desc: String) {
        db.insertItem(title, desc)
    }

    internal fun delete(id: Long) {
        db.deleteItem(id)
    }

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()
}