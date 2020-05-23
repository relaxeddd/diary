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

class RepoTask(private val apiTask: ApiTask) {

    private var mDatabase: Database? = DatabaseCreator.getDataBase()

    suspend fun insertTask(item: Task): Response<List<TaskModel>> {
        val dao = DaoTask(mDatabase!!)
        dao.insert(item)
        return Response.Success(dao.select())
    }

    suspend fun getTaskList(): Response<List<TaskModel>> {
        val dao = DaoTask(mDatabase!!)
        val stub = listOf(TaskModel.Impl(22, "testTitle", "testDesc"))
        return Response.Success(stub)
    }

    suspend fun deleteLocation(location: Task): Response<List<TaskModel>> {
        val dao = DaoTask(mDatabase!!)
        dao.delete(location.id)
        return Response.Success(dao.select())
    }

    fun selectFromDb() {
        val dao = DaoTask(mDatabase!!)
        dao.select()
    }

    fun saveAsync(item: Task, success: (List<TaskModel>) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            try {
                val dao = DaoTask(mDatabase!!)
                dao.insert(item)

                val listLocation = getTaskList() as Response.Success
                success(listLocation.data)
            } catch (ex: Exception) {
                failure(ex)
            }
        }
    }

    fun getLocationListAsync(success: (List<TaskModel>) -> Unit, failure: (Throwable?) -> Unit) {
        GlobalScope.launch(ApplicationDispatcher) {
            val dao = DaoTask(mDatabase!!)
            val items = dao.select()
            success(items)
        }
    }
}