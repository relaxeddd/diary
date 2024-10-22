package relaxeddd.simplediary

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.*
import relaxeddd.simplediary.di.InjectorCommon
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

actual class ContextArgs(var context: Context)

internal actual fun init() {

}

internal actual fun generateId() : String {
    return UUID.randomUUID().toString()
}

internal actual fun registerFirebaseUserListener(listener: (tokenId: String, uid: String, email: String) -> Unit) {
    //TODO
}

internal actual fun checkAuthorization(listener: (tokenId: String, uid: String, email: String) -> Unit) {
    //TODO
}

internal actual fun createFirebaseUser(email: String, password: String,
                              listener: (tokenId: String, uid: String, email: String, errorCode: Int?,
                                         errorDescription: String?) -> Unit) {
    //TODO
}

internal actual fun loginFirebaseUser(email: String, password: String,
                             listener: (tokenId: String, uid: String, email: String, errorCode: Int?,
                                        errorDescription: String?) -> Unit) {
    //TODO
}

internal actual fun logout(listener: (isSuccess: Boolean) -> Unit) {
    //TODO
}

internal actual fun isNetworkAvailable(): Boolean {
    val connectivityManager = InjectorCommon.contextArgs.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

internal actual fun platformName(): String {
    return "Android"
}

internal actual fun getSavedEmail(): String {
    //TODO
    return "test@test.test"
}

internal actual fun setSavedEmail(email: String) {
    //TODO
}

internal actual fun getSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(Database.Schema,  InjectorCommon.contextArgs.context, "relaxeddd.simplediary.diaryDB")
}

internal actual fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

internal actual fun freezeThread(seconds: Int) {
    Thread.sleep((seconds * 1000).toLong())
}

//----------------------------------------------------------------------------------------------------------------------
private val lock = Any()
private var mainHandler: Handler? = null
private val executorService: ExecutorService by lazy { Executors.newFixedThreadPool(4) }

internal actual fun postOnMainThread(run: () -> Unit) {
    if (mainHandler == null) {
        synchronized (lock) {
            if (mainHandler == null) {
                mainHandler = createHandler()
            }
        }
    }
    mainHandler?.post(run)
}

private fun createHandler() : Handler {
    val looper = Looper.getMainLooper()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        return Handler.createAsync(looper)
    }
    try {
        return Handler::class.java.getDeclaredConstructor(Looper::class.java,
            Handler.Callback::class.java, Boolean::class.javaPrimitiveType).newInstance(looper, null, true)
    } catch (ignored: IllegalAccessException) {
    } catch (ignored: InstantiationException) {
    } catch (ignored: NoSuchMethodException) {
    } catch (e: InvocationTargetException) {}

    return Handler(looper)
}

internal actual fun <T> async(run: suspend () -> T?, onCompleted: (T?, Exception?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        var result: T? = null
        var exception: Exception? = null
        try {
            delay(1000)
            run().let {
                result = it
            }
        } catch (e: Exception) {
            exception = e
        }
        withContext(Dispatchers.Main) {
            postOnMainThread { onCompleted(result, exception) }
        }
    }
}
