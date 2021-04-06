package relaxeddd.simplediary.domain

import kotlinx.serialization.Serializable
import relaxeddd.simplediary.TaskModel
import relaxeddd.simplediary.getCurrentTime

@Serializable
data class Task(
    val id: String,
    val title: String = "",
    val desc: String = "",
    val comment: String = "",
    val location: String = "",
    val priority: Int = 0,
    val repeat: Int = 0,
    val repeatCount: Int = 0,
    private val startDate: Long? = null,
    private val endDate: Long? = null,
    val untilDate: Long = 0,
    val isPersistent: Boolean = false,
    val isCompleted: Boolean = false,
    val exDates: List<Long> = ArrayList(),
    val remindHours: List<Int> = ArrayList(),
    val parentId: String = "",
    val isDateTask: Boolean = false
) {
    constructor(taskModel: TaskModel) : this(taskModel.id, taskModel.title, taskModel.desc, taskModel.comment,
        taskModel.location, taskModel.priority, taskModel.repeat, taskModel.repeatCount,
        if (taskModel.start == 0L) getCurrentTime() else taskModel.start,
        if (taskModel.end == 0L) getCurrentTime() else taskModel.end,
        taskModel.until, taskModel.isPersistent, taskModel.isCompleted, taskModel.exDates, taskModel.remindHours, "", false)

    constructor(parentTask: Task, id: String, startDate: Long, endDate: Long)
            : this(id, parentTask.title, parentTask.desc, parentTask.comment, parentTask.location, parentTask.priority,
                   parentTask.repeat, parentTask.repeatCount, startDate, endDate, parentTask.untilDate,
                   parentTask.isPersistent, parentTask.isCompleted, parentTask.exDates, parentTask.remindHours,
                   parentTask.id)

    val start: Long
        get() {
            return startDate ?: if (endDate != null) {
                endDate - TIME_15_MINUTE
            } else {
                getCurrentTime()
            }
        }

    val end: Long
        get() {
            return endDate ?: if (startDate != null) {
                startDate + TIME_15_MINUTE
            } else {
                getCurrentTime() + TIME_15_MINUTE
            }
        }

    fun isChildTask() = parentId.isNotBlank()

    fun isRepetitive() = repeat != RepeatRule.NO.ordinal

    fun isHidden() = exDates.contains(start)
}

enum class RepeatRule {

    NO, DAYLY, WEEKLY, MONTHLY, YEARLY;
}

class Action(private val type: EventType, var args: Map<String, Any>? = null) {

    private var isHandled = false

    fun getTypeIfNotHandled(): EventType? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            type
        }
    }
}

@Serializable
data class User(
    val userId: String = "",
    val email: String = "",
    val subscriptionTime: Long = 0,
    val savedTasksCount: Int = 0
) {
    constructor(user: User) : this(user.userId, user.email, user.subscriptionTime, user.savedTasksCount)

    fun isSubscribed() : Boolean {
        return subscriptionTime > getCurrentTime()
    }
}

@Serializable
internal data class ResultInit(val result: Result?, val user: User?)

@Serializable
internal data class ResultTasks(val result: Result?, val tasks: List<Task> = ArrayList())

@Serializable
internal data class Result(val code: Int = RESULT_UNDEFINED, val message: String = "") {
    fun isSuccess() = code == RESULT_OK
}

internal class Response<out T>(val data: T? = null, val exception: Exception? = null) {

    val isValid: Boolean = exception == null
}

enum class EventType {

    LOADING_SHOW,
    LOADING_HIDE,
    PRESS_BACK,
    EXIT,
    ERROR,
    ALERT,
    GO_SCREEN_LOGIN,
    GO_SCREEN_LIST,
    GOOGLE_AUTH,
    GOOGLE_LOGOUT,

    NAVIGATION_AUTH,

    NAVIGATION_FRAGMENT_TODO_LIST,
    NAVIGATION_FRAGMENT_SETTINGS,

    NAVIGATION_DIALOG_CONFIRM_TASKS_SAVE,
    NAVIGATION_DIALOG_CONFIRM_TASKS_LOAD,
    NAVIGATION_DIALOG_REPETITIVE_TASK_COMPLETE,

    NAVIGATION_DIALOG_APP_ABOUT,
    NAVIGATION_DIALOG_CONFIRM_LOGOUT,
    NAVIGATION_GOOGLE_LOGOUT,
    NAVIGATION_DIALOG_THEME,
    NAVIGATION_RECREATE_ACTIVITY
}
