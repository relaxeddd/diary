@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.StringRes
import relaxeddd.simplediary.R
import relaxeddd.simplediary.domain.Task

@Keep
data class User(

    val userId: String = "",
    val email: String = "",
    var subscriptionTime: Long = 0
) {
    constructor(user: User) : this(user.userId, user.email, user.subscriptionTime)
}

/*@Keep
data class Task(

    val id: String = "",
    val title: String = "",
    val description: String = "",
    val rrule: String = "",
    val importance: Int
) {
    constructor(task: Task) : this(task.id, task.title, task.description, task.rrule, task.importance)
}*/

@Keep
data class ServerAnswer<T>(val code: Int = RESULT_UNDEFINED, val message: String = "", val content: T? = null) {
    val isSuccess: Boolean get() = code == RESULT_OK
    val isFailure: Boolean get() = code != RESULT_OK
}

@Keep
data class InitContent(val user: User?, val tasks: List<Task>? = null, val isActualVersion: Boolean = true)

@Keep
data class UserContent(val user: User?)

@Keep
data class PurchaseContent(val userId: String = "", val tokenId: String = "", val itemType: String = "",
                           val refillContent: RefillContent = RefillContent(), val isObtained: Boolean = false,
                           val text: String = "")

@Keep
data class RefillContent(val subscriptionTime: Long = 0)

enum class EventType {

    LOADING_SHOW,
    LOADING_HIDE,
    PRESS_BACK,
    EXIT,
    GOOGLE_AUTH,
    GOOGLE_LOGOUT,

    NAVIGATION_FRAGMENT_TODO_LIST,
    NAVIGATION_FRAGMENT_SETTINGS,

    NAVIGATION_DIALOG_APP_ABOUT,
    NAVIGATION_DIALOG_RECEIVE_HELP,
    NAVIGATION_DIALOG_CONFIRM_LOGOUT,
    NAVIGATION_GOOGLE_LOGOUT,
    NAVIGATION_WEB_PLAY_MARKET,
    NAVIGATION_DIALOG_THEME,
    NAVIGATION_RECREATE_ACTIVITY
}

@Keep
data class Resource<T>(

    val value: T? = null,
    val status: Int = RESULT_UNDEFINED,
    val errorStr: String = "",
    @StringRes val errorResIdStr: Int = R.string.undefined_error
) {
    fun isSuccess() = status == RESULT_OK
}

@Keep
class NavigationEvent(private val type: EventType, var args: Bundle? = null) {

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
