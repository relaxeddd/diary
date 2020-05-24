package relaxeddd.simplediary.source.repository

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import relaxeddd.simplediary.ApplicationDispatcher
import relaxeddd.simplediary.Database
import relaxeddd.simplediary.data.TaskModel
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.source.db.DatabaseCreator
import relaxeddd.simplediary.source.db.dao.DaoTask
import relaxeddd.simplediary.source.network.ApiTask

class RepositoryTasks(private val apiTask: ApiTask) {

    private var database: Database? = DatabaseCreator.getDataBase()

    /*suspend fun insertTask(item: Task): Response<List<Task>> {
        val cachedTasks = database?.let {
            val dao = DaoTask(it)
            dao.insert(item)
            Response.Success(dao.select().map { cachedTask -> Task(cachedTask) })
        }

        return cachedTasks
    }*/

    suspend fun getTasks() = fetchTasksFromDb().let { tasks ->
        if (tasks.isEmpty()) { //TODO check
            apiTask.requestTasks().also { tasksResponse ->
                if (tasksResponse.isValid) {
                    tasksResponse.data?.forEach { insertTaskToCache(it) }
                }
            }
        } else {
            Response(tasks)
        }
    }

    /*suspend fun deleteLocation(location: Task): Response<List<Task>> {
        val dao = DaoTask(database!!)
        dao.delete(location.id)
        return Response.Success(dao.select().map { Task(it) })
    }*/

    /*fun selectFromDb() {
        val dao = DaoTask(database!!)
        dao.select()
    }

    fun saveAsync(item: Task, success: (List<TaskModel>) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val dao = DaoTask(database!!)
                dao.insert(item)

                val listLocation = getTasks() as Response.Success
                success(listLocation.data)
            } catch (ex: Exception) {
                failure(ex)
            }
        }
    }

    fun getLocationListAsync(success: (List<TaskModel>) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            val dao = DaoTask(database!!)
            val items = dao.select()
            success(items)
        }
    }*/

    private suspend fun insertTaskToCache(item: Task) = database?.let { DaoTask(it).insert(item) }

    private suspend fun fetchTasksFromDb() = database?.let { DaoTask(it).select().map { cachedTask -> Task(cachedTask) }} ?: emptyList()
}