@file:Suppress("unused")
package relaxeddd.simplediary.common

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface PreferenceStorage {

    var selectedTheme: String?
    var observableSelectedTheme: LiveData<String>
}

class SharedPreferenceStorage(private val context: Context) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        ).apply {
            registerOnSharedPreferenceChangeListener(changeListener)
        }
    }

    private val observableSelectedThemeResult = MutableLiveData<String>()

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            PREF_DARK_MODE_ENABLED -> observableSelectedThemeResult.value = selectedTheme
        }
    }

    override var selectedTheme by StringPreference(prefs, PREF_DARK_MODE_ENABLED, Theme.SYSTEM.storageKey)

    override var observableSelectedTheme: LiveData<String>
        get() {
            observableSelectedThemeResult.value = selectedTheme
            return observableSelectedThemeResult
        }
        set(_) = throw IllegalAccessException("This property can't be changed")

    fun getPushToken() : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(PUSH_TOKEN, "") ?: ""
    }

    fun setPushToken(pushToken : String) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(PUSH_TOKEN, pushToken).apply()
    }

    fun getLaunchCount() : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(LAUNCH_COUNT, 0)
    }

    fun setLaunchCount(count : Int) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(LAUNCH_COUNT, count).apply()
    }

    /*fun getAppThemeType() : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(APP_THEME, THEME_STANDARD)
    }

    fun setAppThemeType(value : Int) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(APP_THEME, value).apply()
    }*/

    /*fun getSelectedTheme() : String {
        val sPref = context.getSharedPreferences(SELECTED_THEME, Context.MODE_PRIVATE)
        return sPref.getString(USER_EMAIL, "") ?: ""
    }

    fun setSelectedTheme(string : String) {
        val sPref = context.getSharedPreferences(SELECTED_THEME, Context.MODE_PRIVATE)
        sPref.edit().putString(USER_EMAIL, string).apply()
    }*/
}

class BooleanPreference(private val preferences: Lazy<SharedPreferences>, private val name: String,
                        private val defaultValue: Boolean) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(private val preferences: Lazy<SharedPreferences>, private val name: String,
                       private val defaultValue: String?) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}
