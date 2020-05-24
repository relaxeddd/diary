package relaxeddd.simplediary.source.db

import relaxeddd.simplediary.Database
import relaxeddd.simplediary.getSqlDriver

object DatabaseCreator {

    fun getDataBase() = getSqlDriver()?.let { Database(it) }
}