@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.content.Context

object SharedHelper {

    fun isPrivacyPolicyConfirmed(context: Context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)
    }

    fun setPrivacyPolicyConfirmed(isConfirmed : Boolean, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, isConfirmed).apply()
    }

    fun getPushToken(context: Context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(PUSH_TOKEN, "") ?: ""
    }

    fun setPushToken(pushToken : String, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(PUSH_TOKEN, pushToken).apply()
    }

    fun isCancelledRateDialog(context: Context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(CANCELLED_RATE_DIALOG, false)
    }

    fun setCancelledRateDialog(isCancelled: Boolean, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(CANCELLED_RATE_DIALOG, isCancelled).apply()
    }

    fun getLaunchCount(context: Context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(LAUNCH_COUNT, 0)
    }

    fun setLaunchCount(count : Int, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(LAUNCH_COUNT, count).apply()
    }

    fun isPatchNotesViewed(version: String, context: Context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(version, false)
    }

    fun setPatchNotesViewed(version: String, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(version, true).apply()
    }

    fun getUserEmail(context: Context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(USER_EMAIL, "") ?: ""
    }

    fun setUserEmail(string : String, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(USER_EMAIL, string).apply()
    }

    fun getAppThemeType(context: Context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(APP_THEME, THEME_STANDARD)
    }

    fun setAppThemeType(value : Int, context: Context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(APP_THEME, value).apply()
    }
}