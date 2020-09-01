package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import kotlinx.coroutines.*
import platform.UIKit.UIDevice
import platform.darwin.*
import platform.posix.time
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_current_queue
import platform.darwin.dispatch_get_main_queue
import platform.posix.sleep
import kotlin.native.concurrent.*

actual class ContextArgs

actual fun isNetworkAvailable() : Boolean {
    //TODO
    return true
}

actual fun platformName() : String {
    return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
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
            delay(1000)
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
