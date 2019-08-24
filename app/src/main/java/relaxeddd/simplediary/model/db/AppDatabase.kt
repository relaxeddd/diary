package relaxeddd.simplediary.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import relaxeddd.simplediary.common.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}