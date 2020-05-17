package relaxeddd.simplediary.common.theme

import androidx.lifecycle.LiveData
import relaxeddd.simplediary.common.Result
import relaxeddd.simplediary.common.Theme
import relaxeddd.simplediary.common.map

interface ThemedActivityDelegate {

    val theme: LiveData<Theme>
    val currentTheme: Theme
}

class ThemedActivityDelegateImpl(private val observeThemeUseCase: ObserveThemeModeUseCase,
                                 private val getThemeUseCase: GetThemeUseCase) : ThemedActivityDelegate {

    override val theme: LiveData<Theme> by lazy(LazyThreadSafetyMode.NONE) {
        observeThemeUseCase.observe().map {
            if (it is Result.Success) it.data else Theme.SYSTEM
        }
    }

    override val currentTheme: Theme
        get() = getThemeUseCase.executeNow(Unit).let {
            if (it is Result.Success) it.data else Theme.SYSTEM
        }

    init {
        observeThemeUseCase.execute(Unit)
    }
}