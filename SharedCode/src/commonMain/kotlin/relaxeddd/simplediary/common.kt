package relaxeddd.simplediary

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

internal expect fun init()
internal expect fun generateId() : String
internal expect fun registerFirebaseUserListener(listener: (tokenId: String, uid: String, email: String) -> Unit)
internal expect fun checkAuthorization(listener: (tokenId: String, uid: String, email: String) -> Unit)
internal expect fun createFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
internal expect fun loginFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
internal expect fun logout(listener: (isSuccess: Boolean) -> Unit)
internal expect fun platformName(): String
internal expect fun getSavedEmail(): String
internal expect fun setSavedEmail(email: String)
internal expect fun isNetworkAvailable(): Boolean
internal expect fun getSqlDriver(): SqlDriver
internal expect fun getCurrentTime() : Long
internal expect fun freezeThread(seconds: Int)

internal expect fun postOnMainThread(run: () -> Unit)
internal expect fun <T> async(run: suspend () -> T?, onCompleted: (T?, Exception?) -> Unit)

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}

internal fun createDatabase() = Database(getSqlDriver(), taskModelAdapter = TaskModel.Adapter(exDatesAdapter = exDatesAdapter, remindHoursAdapter = remindHoursAdapter))

internal val exDatesAdapter = object : ColumnAdapter<List<Long>, String> {
    override fun decode(databaseValue: String) = if (databaseValue.isBlank()) emptyList() else databaseValue.split(",").map { it.toLong() }
    override fun encode(value: List<Long>) = value.joinToString(separator = ",")
}

internal val remindHoursAdapter = object : ColumnAdapter<List<Int>, String> {
    override fun decode(databaseValue: String) = if (databaseValue.isBlank()) emptyList() else databaseValue.split(",").map { it.toInt() }
    override fun encode(value: List<Int>) = value.joinToString(separator = ",")
}
