package mx.cubiccoding.persistence.database.scoreboard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.cubiccoding.model.dtos.ScoreboardItemPayload

@Entity(tableName = "scoreboard")
data class ScoreboardEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,

    @ColumnInfo(name = "username")
    val username: String?,

    @ColumnInfo(name = "name")
    val name: String?,

    @ColumnInfo(name = "first_surname")
    val firstSurname: String?,

    @ColumnInfo(name = "avatar")
    val avatar: String?,

    @ColumnInfo(name = "userScore")
    val userScore: Int?,

    @ColumnInfo(name = "totalScore")
    val totalScore: Int?,

    @ColumnInfo(name = "rank")
    val rank: Int?,

    @ColumnInfo(name = "email")
    val email: String?
) {
    fun toNetworkPayload() = ScoreboardItemPayload(username, name, firstSurname, userScore, totalScore, avatar, rank, email)
}
