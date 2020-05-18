package relaxeddd.simplediary

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import relaxeddd.simplediary.common.SharedHelper
import relaxeddd.simplediary.ui.main.ViewModelMain
import relaxeddd.simplediary.ui.settings.ViewModelSettings
import relaxeddd.simplediary.ui.todo_list.ViewModelTodoList

class App : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    val appModule = module {

        /*single {
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_SWTECNN)
                .fallbackToDestructiveMigration()
                .build()
        }
        single { ApiHelper() }
        single { RepositoryUsers(get(), get()) }
        single { RepositorySections(context, get()) }*/

        viewModel { ViewModelMain(this@App) }
        viewModel { ViewModelTodoList(this@App) }
        viewModel { ViewModelSettings(this@App) }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FirebaseApp.initializeApp(this)
        SharedHelper.setLaunchCount(SharedHelper.getLaunchCount() + 1)
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}