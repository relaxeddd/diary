package relaxeddd.simplediary.di

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.http.*
import relaxeddd.simplediary.ContextArgs
import relaxeddd.simplediary.createDatabase
import relaxeddd.simplediary.data.db.dao.DaoTask
import relaxeddd.simplediary.data.network.Api
import relaxeddd.simplediary.data.repository.RepositoryTasks
import relaxeddd.simplediary.data.repository.RepositoryUsers
import relaxeddd.simplediary.domain.BASE_ROUTE
import kotlin.native.concurrent.SharedImmutable
import kotlin.native.concurrent.ThreadLocal

internal val database by lazy { createDatabase() }

@SharedImmutable
internal val api by lazy { Api(httpClient) }

@ThreadLocal
internal val daoTask by lazy { DaoTask(database.taskModelQueries) }

internal val repoTasks by lazy { RepositoryTasks(api, daoTask, repoUsers) }

internal val repoUsers by lazy { RepositoryUsers(api) }

internal val httpClient by lazy { HttpClient {
    expectSuccess = false

    defaultRequest {
        url.takeFrom(URLBuilder().takeFrom(BASE_ROUTE).apply { encodedPath += url.encodedPath })
    }
} }

@ThreadLocal
object InjectorCommon {

    lateinit var contextArgs: ContextArgs

    fun provideContextArgs(contextArgs: ContextArgs): ContextArgs {
        this.contextArgs = contextArgs
        return this.contextArgs
    }
}
