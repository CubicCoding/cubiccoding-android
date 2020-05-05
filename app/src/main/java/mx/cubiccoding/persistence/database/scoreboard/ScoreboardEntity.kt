package mx.cubiccoding.persistence.database.scoreboard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.cubiccoding.model.dtos.ScoreboardItemPayload

@Entity(tableName = "scoreboard")
data class ScoreboardEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,

    @ColumnInfo(name = "displayName")
    val displayName: String?,

    @ColumnInfo(name = "avatarUrl")
    val avatarUrl: String?,

    @ColumnInfo(name = "currentScore")
    val currentScore: Float?,

    @ColumnInfo(name = "totalOfferedScore")
    val totalOfferedScore: Int?,

    @ColumnInfo(name = "rank")
    val rank: Int?,

    @ColumnInfo(name = "email")
    val email: String?
) {
    fun toNetworkPayload() = ScoreboardItemPayload(rank, currentScore, totalOfferedScore, displayName, avatarUrl, email)
}
