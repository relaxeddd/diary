package relaxeddd.simplediary.source.db

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.getSqlDriver

//expect fun getSqlDriver(): SqlDriver?

object DatabaseCreator {
    fun getDataBase(): Database? {
        val sqlDriver  = getSqlDriver()
        if (sqlDriver != null) {
            return Database(sqlDriver)
        } else {
            return null
        }
    }
}