package relaxeddd.simplediary.domain.model

import kotlinx.serialization.Serializable
import relaxeddd.simplediary.TaskModel
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.utils.RESULT_OK
import relaxeddd.simplediary.utils.RESULT_UNDEFINED
import relaxeddd.simplediary.utils.TIME_15_MINUTE

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
    val parentId: String = ""
) {
    constructor(taskModel: TaskModel) : this(taskModel.id, taskModel.title, taskModel.desc, taskModel.comment,
        taskModel.location, taskModel.priority, taskModel.repeat, taskModel.repeatCount,
        if (taskModel.start == 0L) getCurrentTime() else taskModel.start,
        if (taskModel.end == 0L) getCurrentTime() else taskModel.end,
        taskModel.until, taskModel.isPersistent, taskModel.isCompleted, taskModel.exDates, taskModel.remindHours, "")

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
data class ResultInit(@Serializable val result: Result?, @Serializable val user: User?)

@Serializable
data class Result(val code: Int = RESULT_UNDEFINED, val message: String = "") {
    fun isSuccess() = code == RESULT_OK
}

enum class EventType {

    LOADING_SHOW,
    LOADING_HIDE,
    PRESS_BACK,
    EXIT,
    ERROR,
    GO_SCREEN_LOGIN,
    GO_SCREEN_LIST,
    INIT_BILLING,
    GOOGLE_AUTH,
    GOOGLE_LOGOUT,
    LAUNCH_BILLING_FLOW,
    BUY_PRODUCT,

    NAVIGATION_AUTH,

    NAVIGATION_FRAGMENT_TODO_LIST,
    NAVIGATION_FRAGMENT_SETTINGS,

    NAVIGATION_DIALOG_RATE_APP,
    NAVIGATION_DIALOG_SEND_FEEDBACK,
    NAVIGATION_DIALOG_PATCH_NOTES,
    NAVIGATION_DIALOG_LIKE_APP,
    NAVIGATION_DIALOG_APP_ABOUT,
    NAVIGATION_DIALOG_SUBSCRIPTION_INFO,
    NAVIGATION_DIALOG_RECEIVE_HELP,
    NAVIGATION_DIALOG_CONFIRM_LOGOUT,
    NAVIGATION_GOOGLE_LOGOUT,
    NAVIGATION_WEB_PLAY_MARKET,
    NAVIGATION_DIALOG_THEME,
    NAVIGATION_DIALOG_SUBSCRIPTION,
    NAVIGATION_RECREATE_ACTIVITY
}
