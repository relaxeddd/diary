package relaxeddd.simplediary.di

import relaxeddd.simplediary.ContextArgs
import relaxeddd.simplediary.source.network.ApiTask
import relaxeddd.simplediary.source.repository.RepositoryTasks
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.ThreadLocal

@SharedImmutable
val apiTask by lazy { ApiTask() }

val repoTasks by lazy { RepositoryTasks() }

@ThreadLocal
object InjectorCommon {

    lateinit var contextArgs: ContextArgs

    fun provideContextArgs(contextArgs: ContextArgs): ContextArgs {
        this.contextArgs = contextArgs
        return this.contextArgs
    }
}
