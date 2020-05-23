package relaxeddd.simplediary.domain.usecase.base

import kotlinx.coroutines.channels.Channel
import relaxeddd.simplediary.domain.Response

abstract class BaseUseCase<R : BaseRequest, T>() {

    var request: R? = null
    private var channel= Channel<Response<T>>(Channel.UNLIMITED)

    suspend fun execute(request: R? = null): Response<T> {
        this.request = request

        val validated = request?.validate() ?: true
        if (validated) return run()
        return Response.Error(IllegalArgumentException())
    }

    abstract suspend fun run(): Response<T>

    fun getChannel() = channel
}