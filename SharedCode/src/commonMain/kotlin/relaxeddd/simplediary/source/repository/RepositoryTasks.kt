package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.async
import relaxeddd.simplediary.di.apiTask
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.freezeThread
//import relaxeddd.simplediary.getDataBase
//import relaxeddd.simplediary.source.db.dao.DaoTask
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class RepositoryTasks() {

    //private val daoTask = DaoTask(getDataBase())
    private var isInitialized = false
    private var isInitializing = false

    private val tasksM = MutableLiveData<List<Task>>(ArrayList())
    val tasks: LiveData<List<Task>> = tasksM

    private val exceptionM = MutableLiveData<Throwable?>(null)
    val exception: LiveData<Throwable?> = exceptionM

    fun init(onCompleted: () -> Unit) {
        if (isInitializing) {
            return
        }
        if (isInitialized) {
            onCompleted()
            return
        }

        isInitializing = true
        async({
            freezeThread(1)
            apiTask.requestTasks()
        }, { tasks: Response<List<Task>?>?, e: Exception? ->
            isInitializing = false
            isInitialized = true
            /*tasksResponse.data?.forEach {
                daoTask.update(
                    it.id, it.title, it.desc, it.priority, it.rrule, it.location, it.start,
                    it.end, it.isCompleted
                )
            }*/
            tasksM.postValue(tasks?.data ?: ArrayList())

            e?.let { exceptionM.postValue(e) } ?: run { tasks?.exception?.let { exceptionM.postValue(e) } ?: run { isInitialized = true } }
            onCompleted()
        })
    }

    fun createTask(title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long, end: Long,
                   isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        val tasks = ArrayList(tasksM.value)

        async({
            freezeThread(1)

            val resultTasks = ArrayList<Task>()
            tasks.forEach { resultTasks.add(it) }

            var id = 0L
            do {
                var isUnique = true
                id += 1
                tasks.find { it.id == id }?.let { isUnique = false }
            } while (!isUnique)
            resultTasks.add(Task(id, title, desc ?: "", priority, rrule ?: "", location ?: "", start, end, isCompleted))

            resultTasks
        }, { resultTasks: ArrayList<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }

    fun deleteTask(id: Long, onCompleted: () -> Unit) {
        val tasks = ArrayList(tasksM.value)

        async({
            freezeThread(1)

            val resultTasks = ArrayList<Task>()
            tasks.forEach { if (it.id != id) resultTasks.add(it) }

            resultTasks
        }, { resultTasks: ArrayList<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted()
        })
    }

    fun updateTask(id: Long, title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long,
                   end: Long, isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        val tasks = ArrayList(tasksM.value)

        async({
            freezeThread(1)

            val resultTasks = ArrayList<Task>()
            tasks.forEach { if (it.id != id) resultTasks.add(it) }
            resultTasks.add(Task(id, title, desc ?: "", priority, rrule ?: "", location ?: "", start, end, isCompleted))

            resultTasks
        }, { resultTasks: ArrayList<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }

    //private suspend fun fetchTasksFromDb() = daoTask.select().map { cachedTask -> Task(cachedTask) } ?: emptyList<Task>()
}
