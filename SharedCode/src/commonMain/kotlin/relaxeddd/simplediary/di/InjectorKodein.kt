package relaxeddd.simplediary.di

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import relaxeddd.simplediary.ApplicationDispatcher
import relaxeddd.simplediary.source.network.ApiTask
import relaxeddd.simplediary.source.repository.RepositoryTasks
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
val KodeinInjector = Kodein {

    bind<CoroutineContext>() with provider { ApplicationDispatcher }
    bind<ApiTask>() with singleton { ApiTask() }
    bind<RepositoryTasks>() with singleton { RepositoryTasks(instance()) }
}
