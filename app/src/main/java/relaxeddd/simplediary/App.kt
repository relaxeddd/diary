package relaxeddd.simplediary

import android.app.Application
import relaxeddd.simplediary.di.InjectorCommon

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        InjectorCommon.provideContextArgs(ContextArgs(this))
    }
}
