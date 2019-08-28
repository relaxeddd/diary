@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentActivity
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R

fun getAppString(@StringRes resId: Int) : String = App.context.getString(resId)

fun getAppString(@StringRes resId: Int, arg: String) : String = App.context.getString(resId, arg)

fun getStringByResName(resName: String): String {
    val packageName = App.context.packageName
    val resId = App.context.resources.getIdentifier(resName, "string", packageName)
    return if (resId != 0) getAppString(resId) else resName
}

fun showToast(string: String) {
    Toast.makeText(App.context, string, Toast.LENGTH_SHORT).show()
}

fun showToast(@StringRes resId: Int) {
    Toast.makeText(App.context, resId, Toast.LENGTH_SHORT).show()
}

fun showToastLong(@StringRes resId: Int) {
    Toast.makeText(App.context, resId, Toast.LENGTH_LONG).show()
}

fun hideKeyboard(view: View) {
    val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun convertDpToPixel(dp: Float): Float {
    return dp * (App.context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

@Suppress("DEPRECATION")
fun isNetworkAvailable(isShowToast: Boolean = true): Boolean {
    val connectivityManager = App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    val isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected

    if (isShowToast && !isNetworkAvailable) {
        showToast(R.string.network_not_available)
    }

    return isNetworkAvailable
}

fun openWebPrivacyPolicy(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/pushenglish"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun openWebApplication(activity: FragmentActivity?) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=relaxeddd.englishnotify"))
    browserIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
    activity?.startActivity(browserIntent)
}

fun getErrorString(code: Int) = getErrorString(Result(code, getAppString(R.string.undefined_error)))

fun getErrorString(result: Result?) : String {
    if (result == null) {
        return getAppString(R.string.undefined_error)
    }

    return when (result.code) {
        RESULT_UNDEFINED -> getAppString(R.string.undefined_error)
        RESULT_ERROR_SEND_FEEDBACK -> getAppString(R.string.feedback_send_error)
        RESULT_ERROR_FEEDBACK_TOO_SHORT -> getAppString(R.string.feedback_too_short)
        RESULT_ERROR_NETWORK -> getAppString(R.string.network_not_available)
        RESULT_LOCAL_ERROR -> getAppString(R.string.undefined_error)
        RESULT_ERROR_UNAUTHORIZED -> getAppString(R.string.unauthorized_error)
        RESULT_ERROR_USER_NOT_FOUND -> getAppString(R.string.user_not_found)
        RESULT_ERROR_APP_INIT -> getAppString(R.string.error_initialization)
        RESULT_ERROR_ADD_PUSH_TOKEN -> getAppString(R.string.error_push_token)
        RESULT_PURCHASE_NOT_VERIFIED -> getAppString(R.string.error_purchase)
        RESULT_PURCHASE_VERIFIED_ERROR -> getAppString(R.string.error_purchase)
        RESULT_PURCHASE_ALREADY_RECEIVED -> getAppString(R.string.error_purchase)
        RESULT_ERROR_INTERNET -> getAppString(R.string.network_not_available)
        else -> result.message
    }
}

fun getPrimaryColorResId() = when (SharedHelper.getAppThemeType()) {
    THEME_BLUE -> R.color.colorPrimary2
    THEME_BLACK -> R.color.colorPrimary3
    else -> R.color.colorPrimary
}

fun getPrimaryDarkColorResId() = when (SharedHelper.getAppThemeType()) {
    THEME_BLUE -> R.color.colorPrimaryDark2
    THEME_BLACK -> R.color.colorPrimary3
    else -> R.color.colorPrimaryDark
}

fun getAccentColorResId() = when (SharedHelper.getAppThemeType()) {
    THEME_BLUE -> R.color.colorAccent2
    THEME_BLACK -> R.color.colorPrimary3
    else -> R.color.colorPrimary
}