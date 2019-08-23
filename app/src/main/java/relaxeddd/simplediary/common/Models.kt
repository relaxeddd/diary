@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.os.Bundle
import androidx.annotation.Keep

@Keep
data class User(

    val userId: String = "",
    val email: String = ""
) {
    constructor(user: User) : this(user.userId, user.email)
}

@Keep
data class Result(val code: Int = RESULT_UNDEFINED, val message: String = "") {
    fun isSuccess() = code == RESULT_OK
}

@Keep
data class PurchaseResult(val result: Result?, val userId: String = "", val tokenId: String = "", val itemType: String = "",
                          val refillInfo: RefillInfo = RefillInfo(), val isObtained: Boolean = false, val text: String = "")

@Keep
data class RefillInfo(val subscriptionTime: Long = 0)

enum class EventType {

    LOADING_SHOW,
    LOADING_HIDE,
    PRESS_BACK,
    EXIT,
    INIT_BILLING,
    GOOGLE_AUTH,
    GOOGLE_LOGOUT,

    NAVIGATION_FRAGMENT_TODO_LIST,
    NAVIGATION_FRAGMENT_SETTINGS,

    NAVIGATION_DIALOG_RATE_APP,
    NAVIGATION_DIALOG_SEND_FEEDBACK,
    NAVIGATION_DIALOG_PATCH_NOTES,
    NAVIGATION_DIALOG_LIKE_APP
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
