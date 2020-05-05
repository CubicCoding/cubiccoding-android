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
    fun getScoreboardUserSummary(email: String, tournamentId: Int): ScoreboardUserSummary {
        Thread.sleep(3000)

        val listOfMultipleOptions = mutableListOf<MultipleOptionsSummaryPayload>()
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 1", listOf("aashdkjahdkhasdjhasd asdkhakdhakjsdh","b askjdhaksjdhaksdhkjashda djadkjhadksjhaksjdhd","caksjdhkajdhkahdk"," askdjhakjdshjkahdsa dshjhagdsd"), listOf(1,3), listOf(1), "2020-05-03T20:33:22", 0.5F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 2", listOf("a","b","c","d"), listOf(2,3), listOf(1), "2020-05-03T20:33:22", 0F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 3", listOf("a","b","c","d"), listOf(1,3), listOf(1, 3), "2020-05-03T20:33:22", 1F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 4", listOf("a","b","c","d"), listOf(1,3), listOf(), "2020-05-03T20:33:22", 0F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 5", listOf("a","b","c","d"), listOf(1,3), listOf(0,1,2,3), "2020-05-03T20:33:22", 0F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 6", listOf("a","b","c","d"), listOf(1,3), listOf(1), "2020-05-03T20:33:22", 0.5F, 400))
        listOfMultipleOptions.add(MultipleOptionsSummaryPayload("Hola 7", listOf("a","b","c","d"), listOf(1,3), listOf(1), "2020-05-03T20:33:22", 0.5F, 400))
        val listOfChallenges = mutableListOf<ChallengeSummaryPayload>()
        listOfChallenges.add(ChallengeSummaryPayload("Adios 1", 1000, "2020-05-03T20:33:22", .3F, "Description!!!"))
        listOfChallenges.add(ChallengeSummaryPayload("Adios 2", 1000, "2020-05-03T20:33:22", .3F, "Description!!!"))
        listOfChallenges.add(ChallengeSummaryPayload("Adios 3", 1000, "2020-05-03T20:33:22", .3F, "Description!!!"))
        listOfChallenges.add(ChallengeSummaryPayload("Adios 4", 1000, "2020-05-03T20:33:22", .3F, "Description!!!"))
        listOfChallenges.add(ChallengeSummaryPayload("Adios 5", 1000, "2020-05-03T20:33:22", .3F, "Description!!!"))

        return ScoreboardUserSummary(listOfMultipleOptions, listOfChallenges)
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
