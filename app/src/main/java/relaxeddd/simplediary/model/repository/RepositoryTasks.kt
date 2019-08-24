package relaxeddd.simplediary.model.repository

import relaxeddd.simplediary.common.SharedHelper
import relaxeddd.simplediary.common.Task
import relaxeddd.simplediary.model.db.AppDatabase

class RepositoryTasks(appDatabase: AppDatabase, private val sharedHelper: SharedHelper) {

    private val dao = appDatabase.taskDao()
    fun getTasks() = dao.getLiveDataAll()

    fun updateTasks(words: List<Task>) {
        val existWords = dao.getAll()
        val idsSet = HashSet<String>()
        var isAllExists = true

        existWords.forEach { idsSet.add(it.id) }
        words.forEach { if (!idsSet.contains(it.id)) isAllExists = false; }

        if (!isAllExists || existWords.size != words.size) dao.deleteAll()
        words.forEach {
            dao.insert(it)
        }
    }
}