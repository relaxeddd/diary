package relaxeddd.simplediary

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import relaxeddd.simplediary.common.SharedPreferenceStorage
import relaxeddd.simplediary.common.theme.GetThemeUseCase
import relaxeddd.simplediary.common.theme.ObserveThemeModeUseCase
import relaxeddd.simplediary.common.theme.ThemedActivityDelegateImpl
import relaxeddd.simplediary.di.InjectorCommon
import relaxeddd.simplediary.model.NetworkHelper
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.model.repository.*
import relaxeddd.simplediary.ui.main.ViewModelMain
import relaxeddd.simplediary.ui.settings.ViewModelSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        /*if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }*/
        InjectorCommon.provideContextArgs(ContextArgs(this))
        FirebaseApp.initializeApp(this)

        val sharedHelper = SharedPreferenceStorage(this)
        sharedHelper.setLaunchCount(sharedHelper.getLaunchCount() + 1)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(module {
                factory { NetworkHelper(this@App) }

                single { ApiHelper(get()) }

                single { RepositoryPreferences(sharedHelper) }
                single { RepositoryInit(get(), get()) }
                single { RepositoryCommon(get()) }
                single { RepositoryUsers(get(), get()) }

                single { ThemedActivityDelegateImpl(ObserveThemeModeUseCase(get()), GetThemeUseCase(get())) }

                viewModel { ViewModelMain(this@App, get(), get(), get(), get(), get()) }
                viewModel { ViewModelSettings(this@App, get(), get()) }
            })
        }
    }
}