package relaxeddd.simplediary.model.repository

import androidx.lifecycle.LiveData
import relaxeddd.simplediary.common.Task
import relaxeddd.simplediary.model.db.AppDatabase

class RepositoryTasks(appDatabase: AppDatabase, repositoryInit: RepositoryInit) {

    private val dao = appDatabase.taskDao()
    val tasks: LiveData<List<Task>> get() = dao.getLiveDataAll()

    init {
        repositoryInit.liveDataInitResult.observeForever {
            if (it?.result != null && it.result.isSuccess() && it.tasks != null) {
                updateTasks(it.tasks)
            }
        }
    }

    private fun updateTasks(items: List<Task>) {
        val existWords = dao.getAll()

        if (!items.containsAll(existWords) || !existWords.containsAll(items)) {
            dao.deleteAll()
        }
        dao.insertAll(items)
    }
}