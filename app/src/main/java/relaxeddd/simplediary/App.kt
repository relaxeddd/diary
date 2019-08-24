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
import relaxeddd.simplediary.model.repository.RepositoryCommon
import relaxeddd.simplediary.model.repository.RepositoryTasks
import relaxeddd.simplediary.model.repository.RepositoryUsers
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
        single { RepositoryCommon(get()) }
        single { RepositoryTasks(get(), SharedHelper) }
        single { RepositoryUsers(get(), get(), get(), SharedHelper) }

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