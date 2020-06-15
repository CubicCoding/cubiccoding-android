package mx.cubiccoding.persistence.database.timeline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TimelineDao {

    @Query("SELECT * FROM timeline WHERE classroomName=:classroomName LIMIT 1")
    fun getByClassroomName(classroomName: String): TimelineEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(timeline: TimelineEntity)

    @Query("DELETE FROM timeline")
    fun deleteAll()
}
