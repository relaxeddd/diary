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
import relaxeddd.simplediary.common.DATABASE_NAME
import relaxeddd.simplediary.common.SharedHelper
import relaxeddd.simplediary.model.db.AppDatabase
import relaxeddd.simplediary.model.http.ApiHelper
import relaxeddd.simplediary.model.repository.*
import relaxeddd.simplediary.ui.main.ViewModelMain
import relaxeddd.simplediary.ui.settings.ViewModelSettings
import relaxeddd.simplediary.ui.todo_list.ViewModelTodoList

class App : MultiDexApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    private val appModule = module {
        single {
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
        single { ApiHelper() }
        single { RepositoryPreferences(this@App, SharedHelper) }
        single { RepositoryInit(get(), get()) }
        single { RepositoryCommon(get()) }
        single { RepositoryTasks(get(), get()) }
        single { RepositoryUsers(get(), get()) }

        viewModel { ViewModelMain(this@App, get(), get(), get(), get()) }
        viewModel { ViewModelTodoList(this@App, get()) }
        viewModel { ViewModelSettings(this@App, get(), get()) }
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