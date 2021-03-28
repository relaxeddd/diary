package relaxeddd.simplediary.viewmodel

import relaxeddd.simplediary.di.repoUsers

object ViewModelCreator {

    fun createAuthViewModel() : IViewModelAuth {
        return ViewModelAuth(repoUsers)
    }

    // TODO 1. inject dependencies of other view models via constructor like in ViewModelAuth
    // TODO 2. Make other view models internal
    // TODO 3. Expose other view models by ViewModelCreator and consume using ViewModelCreator
    // TODO 4. Make MutableObservable internal
}
