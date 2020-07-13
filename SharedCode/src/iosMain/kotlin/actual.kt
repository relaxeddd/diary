package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import platform.UIKit.UIDevice
import relaxeddd.simplediary.di.InjectorCommon
import kotlinx.coroutines.*
import platform.darwin.*
import kotlin.coroutines.CoroutineContext

actual class ContextArgs

actual fun isNetworkAvailable() : Boolean {
    //TODO
    return true
}

actual fun platformName() : String {
    return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getSqlDriver(): SqlDriver? {
    val driver: SqlDriver = NativeSqliteDriver(Database.Schema, "relaxeddd.diary.db")
    return driver
}

internal actual val ApplicationDispatcher: CoroutineContext = NsQueueDispatcher(dispatch_get_main_queue())

@UseExperimental(InternalCoroutinesApi::class)
internal class NsQueueDispatcher(private val dispatchQueue: dispatch_queue_t) : CoroutineDispatcher(), Delay {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }

    @InternalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatchQueue) {
            try {
                with(continuation) {
                    resumeUndispatched(Unit)
                }
            } catch (err: Throwable) {
                //logError("UNCAUGHT", err.message ?: "", err)
                throw err
            }
        }
    }

    @InternalCoroutinesApi
    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        val handle = object : DisposableHandle {
            var disposed = false
                private set

            override fun dispose() {
                disposed = true
            }
        }
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, timeMillis * 1_000_000), dispatch_get_main_queue()) {
            try {
                if (!handle.disposed) {
                    block.run()
                }
            } catch (err: Throwable) {
                //logError("UNCAUGHT", err.message ?: "", err)
                throw err
            }
        }

        return handle
    }
}
