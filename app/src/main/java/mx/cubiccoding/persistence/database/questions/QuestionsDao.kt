package mx.cubiccoding.persistence.database.questions

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuestionsDao {

    @Query("SELECT * FROM questions WHERE testUuid =:testUuid")
    fun getQuestion(testUuid: String): QuestionEntity?

    @Query("UPDATE questions SET isAnswered = :isAnswered WHERE testUuid = :testUuid")
    fun updateIsAnswered(testUuid: String, isAnswered: Boolean)

    @Query("UPDATE questions SET answered = :answered WHERE testUuid = :testUuid")
    fun updateAnswered(testUuid: String, answered: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(questions: QuestionEntity)
}
