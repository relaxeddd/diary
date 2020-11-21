package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.async
import relaxeddd.simplediary.di.apiTask
import relaxeddd.simplediary.di.daoTask
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Task
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
                        daoTask.update(it.id, it.title, it.desc, it.comment, it.location, it.priority,
                                       it.repeat, it.repeatCount, it.start, it.end, it.untilDate, it.isPersistent,
                                       it.isCompleted, it.exDates, it.remindHours)
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

    fun createTask(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                   repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                   isCompleted: Boolean, remindHours: List<Int>,
                   onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        async({
            daoTask.create(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                           isPersistent, isCompleted, ArrayList(), remindHours)
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

    fun updateTask(id: String, title: String, desc: String, comment: String, location: String, priority: Int,
                   repeat: Int, repeatCount: Int, start: Long, end: Long, until: Long, isPersistent: Boolean,
                   isCompleted: Boolean, exDates: List<Long>, remindHours: List<Int>,
                   onCompleted: ((Response<List<Task>>) -> Unit)? = null) {
        async({
            daoTask.update(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                           isPersistent, isCompleted, exDates, remindHours)
            ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
        }, { resultTasks: List<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }
}
