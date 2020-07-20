package relaxeddd.simplediary

import android.content.Context
import android.net.ConnectivityManager
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import relaxeddd.simplediary.di.InjectorCommon
import kotlin.coroutines.CoroutineContext

actual class ContextArgs(var context: Context)

actual fun isNetworkAvailable(): Boolean{
    val connectivityManager = InjectorCommon.contextArgs.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
}

actual fun platformName(): String {
    return "Android"
}

actual fun getSqlDriver(): SqlDriver {
    val driver: SqlDriver = AndroidSqliteDriver(Database.Schema,  InjectorCommon.contextArgs.context, "relaxeddd.diary.db")
    return driver
}

internal actual val ApplicationDispatcher: CoroutineContext = Dispatchers.Default
