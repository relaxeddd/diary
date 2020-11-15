package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver

expect class ContextArgs

expect fun init()
expect fun registerFirebaseUserListener(listener: (uid: String, email: String) -> Unit)
expect fun isAuthorized() : Triple<Boolean, String, String>
expect fun createFirebaseUser(email: String, password: String, listener: (uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
expect fun loginFirebaseUser(email: String, password: String, listener: (uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit)
expect fun platformName(): String
expect fun isNetworkAvailable(): Boolean
expect fun getSqlDriver(): SqlDriver
expect fun getCurrentTime() : Long
expect fun freezeThread(seconds: Int)

expect fun postOnMainThread(run: () -> Unit)
expect fun <T> async(run: suspend () -> T?, onCompleted: (T?, Exception?) -> Unit)

fun createApplicationScreenMessage() : String {
    return "Kotlin Rocks on ${platformName()}"
}

fun getDataBase() = Database(getSqlDriver())
