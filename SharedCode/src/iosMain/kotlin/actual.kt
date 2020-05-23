package relaxeddd.simplediary

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.ios.NativeSqliteDriver
import platform.UIKit.UIDevice
import relaxeddd.simplediary.di.InjectorCommon

actual class ContextArgs

actual fun isNetworkAvailable() : Boolean {
    //TODO
    return true
}

actual fun platformName() : String {
    return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getSqlDriver(): SqlDriver? {
    val driver: SqlDriver = AndroidSqliteDriver(Database.Schema,  InjectorCommon.contextArgs.context, "relaxeddd.diary.db")
    return driver
}
