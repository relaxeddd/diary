package relaxeddd.simplediary

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import relaxeddd.simplediary.di.InjectorCommon
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

actual class ContextArgs(var context: Context)

actual fun isNetworkAvailable(): Boolean {
    val connectivityManager = InjectorCommon.contextArgs.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

actual fun platformName(): String {
    return "Android"
}

actual fun getSqlDriver(): SqlDriver {
    return AndroidSqliteDriver(Database.Schema,  InjectorCommon.contextArgs.context, "relaxeddd.simplediary.diaryDB")
}

actual fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

actual fun freezeThread(seconds: Int) {
    Thread.sleep((seconds * 1000).toLong())
}

//----------------------------------------------------------------------------------------------------------------------
private val lock = Any()
private var mainHandler: Handler? = null
private val executorService: ExecutorService by lazy { Executors.newFixedThreadPool(4) }

actual fun postOnMainThread(run: () -> Unit) {
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

actual fun <T> async(run: () -> T?, onCompleted: (T?, Exception?) -> Unit) {
    executorService.execute {
        var result: T? = null
        var exception: Exception? = null
        try {
            run().let {
                result = it
            }
        } catch (e: Exception) {
            exception = e
        }
        postOnMainThread { onCompleted(result, exception) }
    }
}
