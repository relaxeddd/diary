package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.async
import relaxeddd.simplediary.di.apiTask
import relaxeddd.simplediary.di.daoTask
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
import relaxeddd.simplediary.freezeThread
import relaxeddd.simplediary.utils.live_data.LiveData
import relaxeddd.simplediary.utils.live_data.MutableLiveData

class RepositoryTasks {

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
            val tasks = ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }

            if (tasks.isEmpty()) {
                val answerTasks = apiTask.requestTasks()

                if (answerTasks.isValid) {
                    ArrayList(answerTasks.data ?: ArrayList()).forEach {
                        daoTask.update(
                            it.id, it.title, it.desc, it.priority, it.rrule, it.location, it.start,
                            it.end, it.isCompleted
                        )
                    }
                }
                answerTasks
            } else {
                Response(tasks)
            }
        }, { tasks: Response<List<Task>?>?, e: Exception? ->
            isInitializing = false
            isInitialized = true

            tasksM.postValue(ArrayList(tasks?.data ?: ArrayList()))
            e?.let { exceptionM.postValue(e) } ?: run { tasks?.exception?.let { exceptionM.postValue(e) } ?: run { isInitialized = true } }
            onCompleted()
        })
    }

    fun createTask(id: String, title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long, end: Long,
                   isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        async({
            daoTask.create(id, title, desc ?: "", priority, rrule ?: "", location ?: "", start, end, isCompleted)
            ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
        }, { resultTasks: List<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }

    fun deleteTask(id: String, onCompleted: () -> Unit) {
        async({
            daoTask.delete(id)
            ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
        }, { resultTasks: List<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted()
        })
    }

    fun updateTask(id: String, title: String, desc: String?, priority: Int, rrule: String?, location: String?, start: Long,
                   end: Long, isCompleted: Boolean, onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        async({
            daoTask.update(id, title, desc ?: "", priority, rrule ?: "", location ?: "", start, end, isCompleted)
            ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
        }, { resultTasks: List<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }
}
