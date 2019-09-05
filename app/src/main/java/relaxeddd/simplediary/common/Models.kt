@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.os.Bundle
import androidx.annotation.Keep
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import relaxeddd.simplediary.R

@Keep
data class User(

    val userId: String = "",
    val email: String = "",
    var subscriptionTime: Long = 0
) {
    constructor(user: User) : this(user.userId, user.email, user.subscriptionTime)
}

@Entity(tableName = TASKS)
@Keep
data class Task(

    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val rrule: String = "",
    val importance: Int
) {
    constructor(task: Task) : this(task.id, task.title, task.description, task.rrule, task.importance)
}

@Keep
data class Result<T>(val code: Int = RESULT_UNDEFINED, val message: String = "", val content: T? = null) {
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
    NAVIGATION_DIALOG_SUBSCRIPTION
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
open class NavigationEvent(private val type: EventType, var args: Bundle? = null) {

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
