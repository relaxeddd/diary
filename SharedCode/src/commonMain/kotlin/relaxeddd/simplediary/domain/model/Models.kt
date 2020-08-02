package relaxeddd.simplediary.domain.model

import kotlinx.serialization.Serializable
import relaxeddd.simplediary.data.TaskModel

@Serializable
data class Task(
    val id: Long,
    val title: String = "",
    val desc: String = "",
    val priority: Int = 0,
    val rrule: String = "",
    val location: String = "",
    val start: Long? = null,
    val end: Long? = null
) {
    constructor(taskModel: TaskModel) : this(taskModel.id, taskModel.title, taskModel.desc ?: "",
        taskModel.priority, taskModel.rrule ?: "", taskModel.location ?: "",
        taskModel.start, taskModel.end)
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
    INIT_BILLING,
    GOOGLE_AUTH,
    GOOGLE_LOGOUT,
    LAUNCH_BILLING_FLOW,
    BUY_PRODUCT,

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
