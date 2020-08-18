package relaxeddd.simplediary.source.repository

import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import kotlinx.coroutines.*
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.getDataBase
import relaxeddd.simplediary.source.db.dao.DaoTask
import relaxeddd.simplediary.source.network.ApiTask

class RepositoryTasks(private val apiTask: ApiTask) {

    private val daoTask = DaoTask(getDataBase())
    private var isInitialized = false

    private val tasksM = MutableLiveData<List<Task>>(ArrayList())
    val tasks: LiveData<List<Task>> = tasksM

    private val exceptionM = MutableLiveData<Throwable?>(null)
    val exception: LiveData<Throwable?> = exceptionM

    private val callback = MutableLiveData<Runnable?>(null).apply {
        addObserver {
            it?.run()
        }
    }

    suspend fun init() {
        if (isInitialized) {
            return
        }

        delay(1000) //TODO for testing
        try {
            fetchTasksFromDb().let { tasks ->
                if (tasks.isEmpty()) { //TODO true request
                    apiTask.requestTasks().also { tasksResponse ->
                        if (tasksResponse.isValid) {
                            isInitialized = true
                            tasksResponse.data?.forEach {
                                daoTask.update(
                                    it.id, it.title, it.desc, it.priority, it.rrule, it.location, it.start,
                                    it.end, it.isCompleted
                                )
                            }
                            tasksM.postValue(tasksResponse.data ?: ArrayList())
                        } else {
                            exceptionM.postValue(tasksResponse.exception)
                        }
                    }
                } else {
                    isInitialized = true
                    tasksM.postValue(tasks)
                }
                //exceptionM.postValue(Exception("Test exception"))
            }
        } catch (e: Exception) {
            exceptionM.postValue(e)
        }
    }

    suspend fun createTask(title: String, desc: String? = null, priority: Int = 0, rrule: String? = null,
                   location: String? = null, start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<Unit>) -> Unit)? = null) {
        performDbOperation(onCompleted) { daoTask.create(title, desc, priority, rrule, location, start, end, isCompleted) }
    }

    suspend fun deleteTask(id: Long) {
        performDbOperation { daoTask.delete(id) }
    }

    suspend fun updateTask(id: Long, title: String, desc: String? = null, priority: Int = 0, rrule: String? = null,
                   location: String? = null, start: Long, end: Long, isCompleted: Boolean, onCompleted: ((Response<Unit>) -> Unit)? = null) {
        performDbOperation(onCompleted) { daoTask.update(id, title, desc, priority, rrule, location, start, end, isCompleted) }
    }

    private suspend fun fetchTasksFromDb() = daoTask.select().map { cachedTask -> Task(cachedTask) } ?: emptyList<Task>()

    private suspend fun performDbOperation(onCompleted: ((Response<Unit>) -> Unit)? = null, operation: suspend () -> Unit) {
        try {
            operation()
            delay(1000) //TODO for testing
            tasksM.postValue(fetchTasksFromDb())
            callback.postValue(object: Runnable {
                override fun run() {
                    onCompleted?.invoke(Response())
                }
            })
        } catch (e: Exception) {
            exceptionM.postValue(e)
            callback.postValue(object: Runnable {
                override fun run() {
                    onCompleted?.invoke(Response(exception = e))
                }
            })
        }
    }
}
