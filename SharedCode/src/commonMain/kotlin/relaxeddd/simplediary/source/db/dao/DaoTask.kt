package relaxeddd.simplediary.source.db.dao

/*class DaoTask(database: Database) {

    private val db = database.taskModelQueries

    internal fun select() : List<TaskModel> = db.selectAll().executeAsList()

    internal fun create(title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long,
                        end: Long, isCompleted: Boolean) : Long {
        db.insertItem(title, desc, priority, rrule, location, start, end, isCompleted)
        return db.lastInsertRowId().executeAsOne()
    }

    internal fun update(id: Long, title: String, desc: String?, priority: Int, rrule: String?, location: String?,
                        start: Long, end: Long, isCompleted: Boolean) {
        db.updateItem(id, title, desc, priority, rrule, location, start, end, isCompleted)
    }

    internal fun delete(id: Long) {
        db.deleteItem(id)
    }
}*/
