package relaxeddd.simplediary.di

import relaxeddd.simplediary.ApplicationDispatcher
import relaxeddd.simplediary.ContextArgs
import relaxeddd.simplediary.source.network.ApiTask
import relaxeddd.simplediary.source.repository.RepositoryTasks
import kotlin.native.concurrent.ThreadLocal

private val apiTask by lazy { ApiTask() }

val coroutineCtx get() = ApplicationDispatcher
val repoTasks by lazy { RepositoryTasks(apiTask) }

@ThreadLocal
object InjectorCommon {

    lateinit var contextArgs: ContextArgs

    fun provideContextArgs(contextArgs: ContextArgs): ContextArgs {
        this.contextArgs = contextArgs
        return this.contextArgs
    }
}
