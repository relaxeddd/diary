package relaxeddd.simplediary.common.theme

import relaxeddd.simplediary.common.MediatorUseCase
import relaxeddd.simplediary.common.Result
import relaxeddd.simplediary.common.Theme
import relaxeddd.simplediary.common.themeFromStorageKey
import relaxeddd.simplediary.model.repository.RepositoryPreferences

open class ObserveThemeModeUseCase(private val repositoryPreferences: RepositoryPreferences)
        : MediatorUseCase<Unit, Theme>() {

    override fun execute(parameters: Unit) {
        result.addSource(repositoryPreferences.observableSelectedTheme) {
            result.postValue(Result.Success(themeFromStorageKey(it)))
        }
    }
}