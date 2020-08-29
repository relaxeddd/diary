package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

expect fun platformName(): String
expect fun isNetworkAvailable(): Boolean
expect fun getSqlDriver(): SqlDriver
expect fun getCurrentTime() : Long
expect fun freezeThread(seconds: Int)

expect fun postOnMainThread(run: () -> Unit)
expect fun <T> async(run: () -> T?, onCompleted: (T?, Exception?) -> Unit)

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}

fun getDataBase() = Database(getSqlDriver())
