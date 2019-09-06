package relaxeddd.simplediary.ui

import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.simplediary.App
import relaxeddd.simplediary.R
import relaxeddd.simplediary.common.*
import relaxeddd.simplediary.model.repository.RepositoryPreferences

open class ViewModelBase(app: App, protected val preferences: RepositoryPreferences) : AndroidViewModel(app) {

    protected val uiScope = CoroutineScope(Dispatchers.Main)
    protected val ioScope = CoroutineScope(Dispatchers.IO)
    val navigateEvent = MutableLiveData<NavigationEvent>()

    val appTheme: Int get() = when (preferences.appThemeType) {
        THEME_BLUE -> R.style.AppTheme2
        THEME_BLACK -> R.style.AppTheme3
        else -> R.style.AppTheme
    }
    val primaryColorResId: Int get() = when (preferences.appThemeType) {
        THEME_BLUE -> R.color.colorPrimary2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimary
    }
    val primaryDarkColorResId: Int get() = when (preferences.appThemeType) {
        THEME_BLUE -> R.color.colorPrimaryDark2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimaryDark
    }
    val primaryAccentColorResId: Int get() = when (preferences.appThemeType) {
        THEME_BLUE -> R.color.colorAccent2
        THEME_BLACK -> R.color.colorPrimary3
        else -> R.color.colorPrimary
    }

    open fun onViewResume() {}

    protected fun showErrorIfIncorrect(result: Result<*>) {
        if (result.isFailure) {
            val errorStr = if (result.message.isEmpty()) getErrorStringByCode(result.code) else result.message
            showToast(errorStr)
        }
    }

    protected fun getString(@StringRes resId: Int, arg1: Any? = null, arg2: Any? = null) : String {
        return getApplication<App>().getString(resId, arg1, arg2)
    }

    protected fun getStringByResName(resName: String): String {
        val context = getApplication<App>()
        val packageName = context.packageName
        val resId = context.resources.getIdentifier(resName, "string", packageName)
        return if (resId != 0) getString(resId) else resName
    }

    protected fun showToast(string: String) {
        uiScope.launch {
            Toast.makeText(getApplication<App>(), string, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showToast(@StringRes resId: Int) {
        uiScope.launch {
            Toast.makeText(getApplication<App>(), resId, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showToastLong(@StringRes resId: Int) {
        uiScope.launch {
            Toast.makeText(getApplication<App>(), resId, Toast.LENGTH_LONG).show()
        }
    }

    @Suppress("DEPRECATION")
    protected fun isNetworkAvailable(isShowToast: Boolean = true): Boolean {
        val connectivityManager = getApplication<App>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        val isNetworkAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected

        if (isShowToast && !isNetworkAvailable) {
            showToast(R.string.network_not_available)
        }

        return isNetworkAvailable
    }

    private fun getErrorStringByCode(code: Int) : String {
        return when (code) {
            RESULT_UNDEFINED -> getString(R.string.undefined_error)
            RESULT_ERROR_SEND_FEEDBACK -> getString(R.string.feedback_send_error)
            RESULT_ERROR_FEEDBACK_TOO_SHORT -> getString(R.string.feedback_too_short)
            RESULT_ERROR_NETWORK -> getString(R.string.network_not_available)
            RESULT_LOCAL_ERROR -> getString(R.string.undefined_error)
            RESULT_ERROR_UNAUTHORIZED -> getString(R.string.unauthorized_error)
            RESULT_ERROR_USER_NOT_FOUND -> getString(R.string.user_not_found)
            RESULT_ERROR_APP_INIT -> getString(R.string.error_initialization)
            RESULT_ERROR_ADD_PUSH_TOKEN -> getString(R.string.error_push_token)
            RESULT_PURCHASE_NOT_VERIFIED -> getString(R.string.error_purchase)
            RESULT_PURCHASE_VERIFIED_ERROR -> getString(R.string.error_purchase)
            RESULT_PURCHASE_ALREADY_RECEIVED -> getString(R.string.error_purchase)
            RESULT_ERROR_INTERNET -> getString(R.string.network_not_available)
            RESULT_OK -> ""
            else -> getString(R.string.undefined_error)
        }
    }

    fun convertDpToPixel(dp: Float): Float {
        return dp * (getApplication<App>().resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}