package mx.cubiccoding.front.home.scoreboard.model

import androidx.annotation.WorkerThread
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardRecyclerViewItem
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.model.utils.Constants.Companion.ONE_HOUR_IN_MS
import mx.cubiccoding.persistence.database.CubicCodingDB
import mx.cubiccoding.persistence.database.scoreboard.ScoreboardEntity
import mx.cubiccoding.persistence.preferences.ScoreboardMetadata
import timber.log.Timber

class ScoreRepository {

    companion object {
        const val EXPIRE_SCOREBOARD_CONTENT_AFTER_TIME_IN_MS = ONE_HOUR_IN_MS * 8
    }

    @WorkerThread
    fun getScores(forceNetworkCall: Boolean): ScoreRepositoryResult? {
        Timber.d("Track, getScores")
        return try {
            //Decide whether to go to server or local...
            val scoreboard = CubicCodingDB.getDatabaseInstance().getScoreboardDao().getAll()
            val isScoreboardTableEmpty = scoreboard.isEmpty()

            Timber.d("Track, getScores validation forced: $forceNetworkCall, expired: ${isScoreboardCacheExpired()}, table empty: $isScoreboardTableEmpty")
            if (forceNetworkCall || isScoreboardCacheExpired() || isScoreboardTableEmpty) {
                ScoreboardRequest.getScore()
            } else {
                Timber.d("Track, Get score from database...")
                getScoreFromDatabase(scoreboard)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while getting score in repository")
            null
        }
    }

    private fun isScoreboardCacheExpired(): Boolean {
        return System.currentTimeMillis() > (ScoreboardMetadata.lastNetworkUpdate + EXPIRE_SCOREBOARD_CONTENT_AFTER_TIME_IN_MS)
    }

    @WorkerThread
    private fun getScoreFromDatabase(localScoreboard: List<ScoreboardEntity>): ScoreRepositoryResult {
        val scoreboardItems = localScoreboard.map { entity ->
            val type = when(entity.rank) {
                1 -> ScoreboardRecyclerViewItem.ScoreboardItemType.FIRST_PLACE
                else -> ScoreboardRecyclerViewItem.ScoreboardItemType.NON_FIRST_PLACE
            }
            ScoreboardRecyclerViewItem(type, entity.toNetworkPayload())
        }
        return ScoreRepositoryResult(ScoreboardMetadata.lastActiveTournament, scoreboardItems)//Simulated empty response until we implement rooms...
    }

    data class ScoreRepositoryResult(val tournament: String, val score: List<ScoreboardRecyclerViewItem>)
}