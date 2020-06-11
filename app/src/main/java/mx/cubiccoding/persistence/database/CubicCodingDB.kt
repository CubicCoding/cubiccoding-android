package mx.cubiccoding.persistence.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.model.utils.Constants.Companion.DATABASE_NAME
import mx.cubiccoding.persistence.database.questions.QuestionEntity
import mx.cubiccoding.persistence.database.questions.QuestionsDao
import mx.cubiccoding.persistence.database.scoreboard.ScoreboardDao
import mx.cubiccoding.persistence.database.scoreboard.ScoreboardEntity
import mx.cubiccoding.persistence.database.timeline.TimelineDao

@Database(entities = [ScoreboardEntity::class, QuestionEntity::class], version = 1, exportSchema = false)
abstract class CubicCodingDB: RoomDatabase() {
    abstract fun getScoreboardDao(): ScoreboardDao
    abstract fun getTimelineDao(): TimelineDao
    abstract fun getQuestionDao(): QuestionsDao
    companion object {

        private lateinit var database: CubicCodingDB

        fun getDatabaseInstance(): CubicCodingDB {
            if (!::database.isInitialized) {
                database = Room.databaseBuilder(CubicCodingApplication.instance, CubicCodingDB::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()
            }
            return database
        }
    }
}
