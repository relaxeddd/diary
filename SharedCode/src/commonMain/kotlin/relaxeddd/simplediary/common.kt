package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver
import kotlin.coroutines.CoroutineContext

expect class ContextArgs

expect fun platformName(): String
expect fun isNetworkAvailable(): Boolean
expect fun getSqlDriver(): SqlDriver

internal expect val ApplicationDispatcher: CoroutineContext

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}

fun getDataBase() = Database(getSqlDriver())