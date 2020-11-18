package relaxeddd.simplediary.domain.model

import kotlinx.serialization.Serializable
import relaxeddd.simplediary.TaskModel
import relaxeddd.simplediary.getCurrentTime
import relaxeddd.simplediary.utils.TIME_15_MINUTE

@Serializable
data class Task(
    val id: String,
    val title: String = "",
    val desc: String = "",
    val priority: Int = 0,
    var repeat: Int = 0,
    val location: String = "",
    private val startDate: Long? = null,
    private val endDate: Long? = null,
    val isCompleted: Boolean = false,
    val exDates: List<Long> = ArrayList(),
    val parentId: String = ""
) {
    constructor(taskModel: TaskModel) : this(taskModel.id, taskModel.title, taskModel.desc ?: "",
        taskModel.priority, taskModel.repeat, taskModel.location ?: "",
        if (taskModel.start == 0L) getCurrentTime() else taskModel.start,
        if (taskModel.end == 0L) getCurrentTime() else taskModel.end,
        taskModel.isCompleted, taskModel.exDates, "")

    constructor(parentTask: Task, id: String, startDate: Long, endDate: Long) : this(id, parentTask.title, parentTask.desc,
        parentTask.priority, parentTask.repeat, parentTask.location, startDate, endDate,
        parentTask.isCompleted, parentTask.exDates, parentTask.id)

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
