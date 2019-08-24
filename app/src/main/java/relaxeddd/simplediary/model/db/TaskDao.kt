package relaxeddd.simplediary.model.db

import androidx.lifecycle.LiveData
import androidx.room.*
import relaxeddd.simplediary.common.TASKS
import relaxeddd.simplediary.common.Task

@Dao
interface TaskDao {

    companion object {

        private const val COLLECTION = TASKS
    }

    @Query("SELECT * FROM $COLLECTION")
    fun getLiveDataAll(): LiveData<List<Task>>

    @Query("SELECT * FROM $COLLECTION")
    fun getAll(): List<Task>

    @Query("SELECT * FROM $COLLECTION WHERE id LIKE :id LIMIT 1")
    fun getLiveDataById(id: String): LiveData<Task>

    @Query("SELECT * FROM $COLLECTION WHERE id LIKE :id LIMIT 1")
    fun getById(id: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<Task>)

    @Delete
    fun delete(item: Task)

    @Query("DELETE FROM $COLLECTION")
    fun deleteAll()

    @Query("DELETE FROM $COLLECTION WHERE id = :id")
    fun deleteById(id: String)
}