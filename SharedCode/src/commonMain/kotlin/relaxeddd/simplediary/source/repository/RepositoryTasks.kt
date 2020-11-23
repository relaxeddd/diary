package relaxeddd.simplediary.source.repository

import relaxeddd.simplediary.async
import relaxeddd.simplediary.di.apiHelper
import relaxeddd.simplediary.di.daoTask
import relaxeddd.simplediary.di.repoUsers
import relaxeddd.simplediary.domain.Response
import relaxeddd.simplediary.domain.model.Result
import relaxeddd.simplediary.domain.model.ResultTasks
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
        async(run = {
            val tasks = ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
            Response(tasks)
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
        async(run = {
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
        async(run = {
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
        async(run = {
            daoTask.update(id, title, desc, comment, location, priority, repeat, repeatCount, start, end, until,
                           isPersistent, isCompleted, exDates, remindHours)
            ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
        }, { resultTasks: List<Task>?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            tasksM.postValue(resultTasks ?: emptyList())
            onCompleted?.invoke(Response(resultTasks, e))
        })
    }

    fun requestSaveTasks(onCompleted: (Result?) -> Unit) {
        async(run = {
            val tasks = ArrayList(daoTask.select()).map { cachedTask -> Task(cachedTask) }
            apiHelper.requestSaveTasks(repoUsers.tokenId.value, repoUsers.uid.value, tasks)
        }, { result: Result?, e: Exception? ->
            e?.let { exceptionM.postValue(e) }
            onCompleted(result)
        })
    }

    fun requestLoadTasks(onCompleted: (Result?) -> Unit) {
        async(run = {
            val resultTasks = apiHelper.requestLoadTasks(repoUsers.tokenId.value, repoUsers.uid.value)
            if (resultTasks.result?.isSuccess() == true) {
                ArrayList(resultTasks.tasks).forEach {
                    daoTask.update(it.id, it.title, it.desc, it.comment, it.location, it.priority,
                                   it.repeat, it.repeatCount, it.start, it.end, it.untilDate, it.isPersistent,
                                   it.isCompleted, it.exDates, it.remindHours)
                }
            }
            resultTasks
        }, { resultTasks: ResultTasks?, e: Exception? ->
            if (e == null && resultTasks?.result?.isSuccess() == true) {
                tasksM.postValue(ArrayList(resultTasks.tasks))
            }
            e?.let { exceptionM.postValue(e) }

            onCompleted(resultTasks?.result)
        })
    }
}
