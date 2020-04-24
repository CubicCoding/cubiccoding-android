package mx.cubiccoding.persistence.database.questions

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(

    @PrimaryKey
    @ColumnInfo(name = "testUuid")
    val testUuid: String,

    @ColumnInfo(name = "label")
    val label: String?,

    @ColumnInfo(name = "questionTitle")
    val questionTitle: String?,

    @ColumnInfo(name = "options")
    val options: String?,

    @ColumnInfo(name = "answers")
    val answers: String?,

    @ColumnInfo(name = "answered")
    val answered: String?,

    @ColumnInfo(name = "maxScore")
    val maxScore: Int?,

    @ColumnInfo(name = "isAnswered")
    val isAnswered: Boolean? = false
)
