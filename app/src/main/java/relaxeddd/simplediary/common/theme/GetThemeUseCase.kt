package relaxeddd.simplediary.common.theme

import androidx.core.os.BuildCompat
import relaxeddd.simplediary.common.Theme
import relaxeddd.simplediary.common.UseCase
import relaxeddd.simplediary.common.themeFromStorageKey
import relaxeddd.simplediary.model.repository.RepositoryPreferences

open class GetThemeUseCase(private val repositoryPreferences: RepositoryPreferences) : UseCase<Unit, Theme>() {

    override fun execute(parameters: Unit): Theme {
        repositoryPreferences.selectedTheme?.let { key ->
            return themeFromStorageKey(key)
        }

        return when {
            BuildCompat.isAtLeastQ() -> Theme.SYSTEM
            else -> Theme.BATTERY_SAVER
        }
    }
}