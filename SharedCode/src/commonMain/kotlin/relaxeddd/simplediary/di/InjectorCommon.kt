package relaxeddd.simplediary.di

import relaxeddd.simplediary.ContextArgs
import relaxeddd.simplediary.getDataBase
import relaxeddd.simplediary.source.db.dao.DaoTask
import relaxeddd.simplediary.source.network.ApiTask
import relaxeddd.simplediary.source.repository.RepositoryTasks
import relaxeddd.simplediary.source.repository.RepositoryUsers
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.ThreadLocal

@SharedImmutable
val apiTask by lazy { ApiTask() }

@ThreadLocal
val daoTask by lazy { DaoTask(getDataBase()) }

val repoTasks by lazy { RepositoryTasks() }

val repoUsers by lazy { RepositoryUsers() }

@ThreadLocal
object InjectorCommon {

    lateinit var contextArgs: ContextArgs

    fun provideContextArgs(contextArgs: ContextArgs): ContextArgs {
        this.contextArgs = contextArgs
        return this.contextArgs
    }
}
