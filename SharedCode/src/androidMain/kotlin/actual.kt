package relaxeddd.simplediary

//import com.squareup.sqldelight.android.AndroidSqliteDriver
//import com.squareup.sqldelight.db.SqlDriver
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import relaxeddd.simplediary.di.InjectorCommon
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext

actual class ContextArgs(var context: Context)

actual fun isNetworkAvailable(): Boolean {
    val connectivityManager = InjectorCommon.contextArgs.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

actual fun platformName(): String {
    return "Android"
}

/*actual fun getSqlDriver(): SqlDriver {
    val driver: SqlDriver = AndroidSqliteDriver(Database.Schema,  InjectorCommon.contextArgs.context, "relaxeddd.diary.db")
    return driver
}*/

actual fun getCurrentTime() : Long {
    return System.currentTimeMillis()
}

internal actual val ApplicationDispatcher: CoroutineContext = Dispatchers.Default

private val lock = Any()
private var mainHandler: Handler? = null

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

    if (Build.VERSION.SDK_INT >= 28) {
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
