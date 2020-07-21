package relaxeddd.simplediary.source.repository

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.delay
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.getDataBase
import relaxeddd.simplediary.source.db.dao.DaoTask
import relaxeddd.simplediary.source.network.ApiTask

class RepositoryTasks(private val apiTask: ApiTask) {

    private val daoTask = DaoTask(getDataBase())
    private var isInitialized = false

    private val tasks_ = MutableLiveData<List<Task>>(ArrayList())
    val tasks: LiveData<List<Task>> = tasks_

    private val exception_ = MutableLiveData<Throwable?>(null)
    val exception: LiveData<Throwable?> = exception_

    suspend fun init() {
        if (isInitialized) {
            tasks_.postValue(tasks_.value)
            return
        }

        delay(1000) //TODO for testing
        fetchTasksFromDb().let { tasks ->
            if (tasks.isEmpty()) {
                apiTask.requestTasks().also { tasksResponse ->
                    if (tasksResponse.isValid) {
                        isInitialized = true
                        tasksResponse.data?.forEach { daoTask.create(it.title, it.desc) }
                        tasks_.postValue(tasksResponse.data ?: ArrayList())
                    } else {
                        exception_.postValue(tasksResponse.exception)
                    }
                }
            } else {
                isInitialized = true
                tasks_.postValue(tasks)
                Response(tasks)
            }
        }
    }

    suspend fun createTask(title: String, desc: String): Response<Unit> {
        return performDbOperation { daoTask.create(title, desc) }
    }

    suspend fun deleteTask(id: Long): Response<Unit> {
        return performDbOperation { daoTask.delete(id) }
    }

    suspend fun updateTask(id: Long, title: String, desc: String): Response<Unit> {
        return performDbOperation { daoTask.update(id, title, desc) }
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

    private suspend fun fetchTasksFromDb() = daoTask.select().map { cachedTask -> Task(cachedTask) } ?: emptyList<Task>()

    private suspend fun performDbOperation(operation: () -> Unit) : Response<Unit> {
        try {
            operation()
            delay(1000) //TODO for testing
            tasks_.postValue(fetchTasksFromDb())
            return Response()
        } catch (e: Exception) {
            return Response(exception = e)
        }
    }
}