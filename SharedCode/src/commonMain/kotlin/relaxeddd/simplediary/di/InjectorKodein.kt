package relaxeddd.simplediary.di

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.provider
import org.kodein.di.erased.singleton
import relaxeddd.simplediary.ApplicationDispatcher
import relaxeddd.simplediary.domain.usecase.task.UseCaseTaskGetList
import relaxeddd.simplediary.source.network.ApiTask
import relaxeddd.simplediary.source.repository.RepoTask
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
val KodeinInjector = Kodein {

    bind<CoroutineContext>() with provider { ApplicationDispatcher }
    bind<ApiTask>() with provider { ApiTask() }

    bind<UseCaseTaskGetList>() with singleton {
        UseCaseTaskGetList(instance())
    }

    bind<RepoTask>() with provider { RepoTask(instance()) }
}