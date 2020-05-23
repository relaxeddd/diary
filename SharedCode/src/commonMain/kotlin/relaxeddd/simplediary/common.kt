package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

expect fun platformName(): String
expect fun isNetworkAvailable(): Boolean
expect fun getSqlDriver(): SqlDriver?

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}