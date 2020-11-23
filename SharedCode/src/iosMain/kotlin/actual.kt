package relaxeddd.simplediary

import cocoapods.FirebaseAuth.FIRAuth
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import kotlinx.coroutines.*
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice
import platform.darwin.*
import platform.posix.time
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_current_queue
import platform.darwin.dispatch_get_main_queue
import platform.posix.sleep
import kotlin.native.concurrent.*

actual class ContextArgs

actual fun init() {

}

actual fun generateId() : String {
    return NSUUID().UUIDString()
}

actual fun registerFirebaseUserListener(listener: (tokenId: String, uid: String, email: String) -> Unit) {
    FIRAuth.auth().addAuthStateDidChangeListener { auth, user ->
        user?.getIDTokenResultWithCompletion { firAuthTokenResult, tokenError ->
            listener(firAuthTokenResult?.token ?: "", user.uid, user.email ?: "")
        }
    }
}


actual fun checkAuthorization(listener: (tokenId: String, uid: String, email: String) -> Unit) {
    val user = FIRAuth.auth().currentUser

    if (user != null) {
        user.getIDTokenResultWithCompletion { firAuthTokenResult, tokenError ->
            listener(firAuthTokenResult?.token ?: "", user.uid, user.email ?: "")
        }
    } else {
        listener("", "", "")
    }
}

actual fun createFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit) {
    FIRAuth.auth().createUserWithEmail(email, password = password) { result, registerError ->
        val user = result?.user

        if (user != null) {
            user.getIDTokenResultWithCompletion { firAuthTokenResult, tokenError ->
                val error = registerError ?: tokenError
                listener(firAuthTokenResult?.token ?: "", user.uid, user.email ?: "", error?.code?.toInt(), error?.description)
            }
        } else {
            listener("", "", "", registerError?.code?.toInt(), registerError?.description)
        }
    }
}

actual fun loginFirebaseUser(email: String, password: String, listener: (tokenId: String, uid: String, email: String, errorCode: Int?, errorDescription: String?) -> Unit) {
    FIRAuth.auth().signInWithEmail(email, password = password) { result, signInError ->
        val user = result?.user

        if (user != null) {
            user.getIDTokenResultWithCompletion { firAuthTokenResult, tokenError ->
                val error = signInError ?: tokenError
                listener(firAuthTokenResult?.token ?: "", user.uid, user.email ?: "", error?.code?.toInt(), error?.description)
            }
        } else {
            listener("", "", "", signInError?.code?.toInt(), signInError?.description)
        }
    }
}

actual fun logout(listener: (isSuccess: Boolean) -> Unit) {
    listener(FIRAuth.auth().signOut(null))
}

actual fun isNetworkAvailable() : Boolean {
    //TODO
    return true
}

actual fun platformName() : String {
    return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getSavedEmail(): String {
    val preferences = NSUserDefaults.standardUserDefaults
    val key = "email"
    return if (preferences.objectForKey(key) == null) "" else preferences.stringForKey(key) ?: ""
}

actual fun setSavedEmail(email: String) {
    val preferences = NSUserDefaults.standardUserDefaults
    val key = "email"
    preferences.setObject(email, key)
    preferences.synchronize()
}

actual fun getSqlDriver(): SqlDriver {
    return NativeSqliteDriver(Database.Schema, "relaxeddd.simplediary.diaryDB")
}

actual fun getCurrentTime() : Long {
    return time(null) * 1000
}

@ExperimentalUnsignedTypes
actual fun freezeThread(seconds: Int) {
    sleep(seconds.toUInt())
}

//----------------------------------------------------------------------------------------------------------------------
actual fun postOnMainThread(run: () -> Unit) {
    val mainQueue = dispatch_get_main_queue()
    if (dispatch_get_current_queue() == mainQueue) {
        run()
    } else {
        dispatch_async(mainQueue) { run() }
    }
}

private val coroutineContext by lazy { NsQueueDispatcher(dispatch_get_main_queue()) }
private val coroutineExceptionHandler by lazy { CoroutineExceptionHandler { _, e -> print(e) } }
private val job: Job = Job()
private val fullCoroutineContext = coroutineContext + job + coroutineExceptionHandler
private const val MAX_ATTEMPTS = 200
private val mapFutureAttempts = HashMap<Future<*>, Int>()

actual fun <T> async(run: suspend () -> T?, onCompleted: (T?, Exception?) -> Unit) {
    CoroutineScope(fullCoroutineContext).launch(fullCoroutineContext, CoroutineStart.DEFAULT) {
        var result: T? = null
        var exception: Exception? = null
        try {
            delay(200)
            run().let {
                result = it
            }
        } catch (e: Exception) {
            exception = e
        }
        postOnMainThread { onCompleted(result, exception) }
    }

    /*val future = Worker.start().execute(TransferMode.SAFE, producer = { run.freeze() }) {
        runBlocking {
            it()
        }
    }
    mapFutureAttempts[future] = 0
    checkFuture(future, onCompleted)*/
}

//TODO Tag for workers to debug
private fun <T> checkFuture(future: Future<T?>, onCompleted: (T?, Exception?) -> Unit) {
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, 50 * 1_000_000), dispatch_get_main_queue()) {
        mapFutureAttempts[future]?.let {
            mapFutureAttempts[future] = it + 1

            if (it < MAX_ATTEMPTS) {
                when(future.state) {
                    FutureState.COMPUTED -> {
                        mapFutureAttempts.remove(future)
                        val result = future.result.freeze()
                        onCompleted(result, null)
                    }
                    FutureState.CANCELLED -> {
                        mapFutureAttempts.remove(future)
                        onCompleted(null, Exception("Worker cancelled"))
                    }
                    FutureState.THROWN -> {
                        mapFutureAttempts.remove(future)
                        onCompleted(null, Exception("Worker thrown"))
                    }
                    FutureState.INVALID -> {
                        mapFutureAttempts.remove(future)
                        onCompleted(null, Exception("Worker invalid"))
                    }
                    else -> checkFuture(future, onCompleted)
                }
            } else {
                mapFutureAttempts.remove(future)
                onCompleted(null, Exception("Worker attempts wasted"))
            }
        }
    }
}
