package mx.cubiccoding.persistence.database.scoreboard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreboardDao {

    @Query("SELECT * FROM scoreboard")
    fun getAll(): List<ScoreboardEntity>

    @Insert
    fun insertAll(vararg scoreboard: ScoreboardEntity)

    @Query("DELETE FROM scoreboard")
    fun deleteAll()
}
