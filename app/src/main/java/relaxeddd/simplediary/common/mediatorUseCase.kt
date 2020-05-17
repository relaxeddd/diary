package relaxeddd.simplediary.common

import androidx.lifecycle.MediatorLiveData

abstract class MediatorUseCase<in P, R> {

    protected val result = MediatorLiveData<Result<R>>()

    // Make this as open so that mock instances can mock this method
    open fun observe() = result

    abstract fun execute(parameters: P)
}