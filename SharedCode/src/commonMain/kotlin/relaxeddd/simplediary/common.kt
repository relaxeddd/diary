package relaxeddd.simplediary

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

expect fun init()
expect fun generateId() : String
expect fun registerFirebaseUserListener(listener: (uid: String, email: String) -> Unit)
expect fun isAuthorized() : Triple<Boolean, String, String>
expect fun createFirebaseUser(email: String, password: String, listener: (uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
expect fun loginFirebaseUser(email: String, password: String, listener: (uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
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

fun getDataBase() = Database(getSqlDriver(), taskModelAdapter = TaskModel.Adapter(exDatesAdapter = exDatesAdapter))

val exDatesAdapter = object : ColumnAdapter<List<Long>, String> {
    override fun decode(databaseValue: String) = databaseValue.split(",").map { it.toLong() }
    override fun encode(value: List<Long>) = value.joinToString(separator = ",")
}
