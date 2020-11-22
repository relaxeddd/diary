package relaxeddd.simplediary

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

expect fun init()
expect fun generateId() : String
expect fun registerFirebaseUserListener(listener: (tokenId: String, uid: String, email: String) -> Unit)
expect fun checkAuthorization(listener: (tokenId: String, uid: String, email: String) -> Unit)
expect fun createFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
expect fun loginFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
expect fun logout(listener: (isSuccess: Boolean) -> Unit)
expect fun platformName(): String
expect fun getSavedEmail(): String
expect fun setSavedEmail(email: String)
expect fun isNetworkAvailable(): Boolean
expect fun getSqlDriver(): SqlDriver
expect fun getCurrentTime() : Long
expect fun freezeThread(seconds: Int)

expect fun postOnMainThread(run: () -> Unit)
expect fun <T> async(run: suspend () -> T?, onCompleted: (T?, Exception?) -> Unit)

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}

fun getDataBase() = Database(getSqlDriver(), taskModelAdapter = TaskModel.Adapter(exDatesAdapter = exDatesAdapter, remindHoursAdapter = remindHoursAdapter))

val exDatesAdapter = object : ColumnAdapter<List<Long>, String> {
    override fun decode(databaseValue: String) = if (databaseValue.isBlank()) emptyList() else databaseValue.split(",").map { it.toLong() }
    override fun encode(value: List<Long>) = value.joinToString(separator = ",")
}

val remindHoursAdapter = object : ColumnAdapter<List<Int>, String> {
    override fun decode(databaseValue: String) = if (databaseValue.isBlank()) emptyList() else databaseValue.split(",").map { it.toInt() }
    override fun encode(value: List<Int>) = value.joinToString(separator = ",")
}
