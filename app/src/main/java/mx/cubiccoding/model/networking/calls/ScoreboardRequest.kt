package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardDataItem
import mx.cubiccoding.model.dtos.*
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.utils.Constants.Companion.HTTP_CONFLICT
import mx.cubiccoding.model.utils.Constants.Companion.HTTP_GONE
import mx.cubiccoding.model.utils.Constants.Companion.HTTP_UNAUTHORIZED
import mx.cubiccoding.persistence.database.CubicCodingDB
import mx.cubiccoding.persistence.database.questions.QuestionEntity
import mx.cubiccoding.persistence.preferences.ScoreboardMetadata
import org.json.JSONArray
import timber.log.Timber

object ScoreboardRequest {

    data class ScoreboardRequestResult(val tournamentName: String, val tournamentId: Int, val score: List<ScoreboardDataItem>)

    @WorkerThread
    fun getScoreboardUserSummary(email: String, tournamentId: Int): ScoreboardUserSummaryPayload {
        val response = RequestsManager.cubicCodingManagerApi.getScoreboardSummaryForUser(email, tournamentId).execute()
        return when {
            response.isSuccessful -> {
                response.body() ?: throw CubicCodingRequestException(
                        "getScoreboardUserSummary request has null body",
                        RequestErrorType.NULL_BODY,
                        response.code()
                    )
            }
            else -> {
                throw CubicCodingRequestException(
                    "getScoreboardUserSummary request not successful",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
            }
        }
    }

    @WorkerThread
    fun getScore(email: String, classroomName: String): ScoreboardRequestResult {
        val response = RequestsManager.cubicCodingManagerApi.getScoreboard(email, classroomName).execute()
        return when {
            response.isSuccessful -> {
                val secondaries = response.body()?.secondaries ?: emptyList()
                val scoreboardItems = secondaries.map { scoreboardItemPayload ->
                    val type = when (scoreboardItemPayload.rank) {
                        1 -> ScoreboardDataItem.ScoreboardItemType.FIRST_PLACE
                        else -> ScoreboardDataItem.ScoreboardItemType.NON_FIRST_PLACE
                    }
                    ScoreboardDataItem(type, scoreboardItemPayload)
                }

                val scoreItemsForDB = secondaries.map { it.toDBEntity() }
                //Store the results into the local caching...
                with(CubicCodingDB.getDatabaseInstance().getScoreboardDao()) {

                    deleteAll()
                    Timber.d("Track, Deleted DB old data")

                    insertAll(*scoreItemsForDB.toTypedArray())
                    Timber.d("Track, Inserted DB new data")

                }

                //TODO: This DB processing could be taken out into the repository to allow the modularity of
                //TODO: making only request without DB, giving the code the chance to do only request(here), only db(dao), both(repository)
                //TODO: rather than coupling requests+db as the only choice...(Look at TimelineRepository/Request/Dao)
                //Update the last time that we successfully returned values...
                val tournamentName = response.body()?.tournamentInfo?.tournamentName ?: ""
                val tournamentId = response.body()?.tournamentInfo?.id ?: 0
                ScoreboardMetadata.lastNetworkUpdate = System.currentTimeMillis()
                ScoreboardMetadata.lastActiveTournamentName = tournamentName
                ScoreboardMetadata.lastActiveTournamentId = tournamentId

                ScoreboardRequestResult(tournamentName, tournamentId, scoreboardItems)
            }
            else -> {
                throw CubicCodingRequestException(
                    "getScore request not successful",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
            }
        }
    }


    @WorkerThread
    fun getTestQuestion(uuid: String): String? {
        val questions = CubicCodingDB.getDatabaseInstance().getQuestionDao().getQuestion(uuid)
        if (questions != null) {
            return questions.testUuid
            Timber.d("Track, Question found in local db...")
        }// Move on with network call if not found...

        val response = RequestsManager.cubicCodingManagerApi.getQuestion(uuid).execute()
        val testQuestions = response.body()
        if (response.isSuccessful && testQuestions != null) {
            //Always store the questions into a table before firing the callback...
            val optionsJsonArray = JSONArray()
            testQuestions.options.forEach {
                optionsJsonArray.put(it)
            }
            val optionsString = optionsJsonArray.toString()
            val answersJsonArray = JSONArray()
            testQuestions.answers.forEach {
                answersJsonArray.put(it)
            }
            val answersString = answersJsonArray.toString()

            Timber.e("Storing into db options: $optionsString, answers: $answersString")
            CubicCodingDB.getDatabaseInstance().getQuestionDao().insert(
                QuestionEntity(
                    testQuestions.uuid ?: "",
                    testQuestions.label,
                    testQuestions.questionTitle,
                    optionsString,
                    answersString,
                    "",
                    testQuestions.maxScore
                )
            )

            return testQuestions.uuid
        } else {
            throw CubicCodingRequestException(
                "GetQuestion request not successful",
                RequestErrorType.UNSUCCESS,
                response.code()
            )
        }
    }

    @WorkerThread
    fun uploadAnswer(testUuid: String, answer: List<Int>): UploadAnswerResult {
        val response = RequestsManager.cubicCodingManagerApi.uploadAnswer(UploadAnswerRequestPayload(testUuid, answer)).execute()
        return when {
            response.isSuccessful -> {
                UploadAnswerResult(UploadAnswerStatus.SUCCESS, response.code())
            }
            else -> {
                val error = CubicCodingRequestException(
                    "UploadAnswer request not successful",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
                Timber.e(error, "ERROR")
                when (response.code()) {
                    HTTP_GONE, HTTP_UNAUTHORIZED, HTTP_CONFLICT -> UploadAnswerResult(
                        UploadAnswerStatus.CANCEL_TASK,
                        response.code()
                    )//These codes are specified in the API documentation as invalid states that we won't be able to recover from, hence cancel task...
                    else -> UploadAnswerResult(
                        UploadAnswerStatus.REQUIRES_RETRY,
                        response.code()
                    ) //If we didn't get success for any other reason, then retry...
                }
            }
        }
    }

    enum class UploadAnswerStatus {
        SUCCESS,
        REQUIRES_RETRY,
        CANCEL_TASK
    }

    data class UploadAnswerResult(val status: UploadAnswerStatus, val code: Int)
}
