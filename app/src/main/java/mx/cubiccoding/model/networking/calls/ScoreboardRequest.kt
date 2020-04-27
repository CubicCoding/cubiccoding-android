package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.front.home.scoreboard.model.ScoreRepository
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardRecyclerViewItem
import mx.cubiccoding.model.dtos.*
import mx.cubiccoding.model.networking.GenericRequestListener
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
import java.util.*

object ScoreboardRequest {

    @WorkerThread
    fun getScore(): ScoreRepository.ScoreRepositoryResult {

        //TODO: Remove fake delay
        Thread.sleep(3000)//Network call simulation

        val mockedResponseItems = mutableListOf<ScoreboardItemPayload>()
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Dennies Ritchie",
                "Dennies",
                "Ritchie",
                120,
                120,
                "https://upload.wikimedia.org/wikipedia/commons/2/23/Dennis_Ritchie_2011.jpg",
                1,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Joshua Bloch",
                "Joshua",
                "Bloch",
                100,
                120,
                "https://avatars0.githubusercontent.com/u/2133870?s=460&u=b7d550ed72a3579b0ba5ab246e582f7b73000b4b&v=4",
                2,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Linus Torvalds",
                "Linus",
                "Torvalds",
                90,
                120,
                "https://i.blogs.es/195f61/linus1/450_1000.jpg",
                3,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "martincaz",
                "Bill",
                "Gates",
                85,
                120,
                "https://cdn1.thr.com/sites/default/files/imagecache/768x433/2018/02/billgatesheadshot_-_h_2018.jpg",
                4,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Mark Zuckerberg",
                "Mark",
                "Zuckerberg",
                80,
                120,
                "https://f0.pngfuel.com/png/506/496/united-states-mark-zuckerberg-facebook-chief-executive-news-feed-mark-zuckerberg-png-clip-art.png",
                5,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Perry Calderon",
                "Perry",
                "Calderon",
                70,
                120,
                "https://scontent.fmzt2-1.fna.fbcdn.net/v/t1.0-9/1930640_38753265589_55_n.jpg?_nc_cat=103&_nc_sid=e007fa&_nc_eui2=AeHvpA-kO9DHqO_mUQnDfxnL0yjAikIicF7TKMCKQiJwXvU6CG-3TrxL-ILTOfejBaE&_nc_ohc=Yg6kDdtOS7kAX_bGcaM&_nc_ht=scontent.fmzt2-1.fna&oh=3a8d3fdfef539389dfdabfb1b6b97f8f&oe=5EBA6D00",
                6,
                "email"
            )
        )
        mockedResponseItems.add(
            ScoreboardItemPayload(
                "Aaron Warxe",
                "Aaron",
                "Warxe",
                60,
                120,
                "https://scontent.fmzt2-1.fna.fbcdn.net/v/t31.0-8/242439_10150282354857209_2064824_o.jpg?_nc_cat=110&_nc_sid=e007fa&_nc_eui2=AeGp-eKxB3KAGfjrhzaFQqHPOihnrvO88vI6KGeu87zy8rsXpgdm2XQZ5TOKVqqrs5k&_nc_ohc=oW9d5HnH5ckAX_93yAA&_nc_ht=scontent.fmzt2-1.fna&oh=3395423c27f501ca00e707ccfea6c746&oe=5EBB9A1F",
                7,
                "email"
            )
        )
        val simulatedResponse = ScoreboardResponsePayload("amazon_gift", mockedResponseItems)

        val scoreboardItems = simulatedResponse.leaders.map { scoreboardItemPayload ->
            val type = when (scoreboardItemPayload.rank) {
                1 -> ScoreboardRecyclerViewItem.ScoreboardItemType.FIRST_PLACE
                else -> ScoreboardRecyclerViewItem.ScoreboardItemType.NON_FIRST_PLACE
            }
            ScoreboardRecyclerViewItem(type, scoreboardItemPayload)
        }

        val scoreItemsForDB = simulatedResponse.leaders.map { it.toDBEntity() }

        //Store the results into the local caching...
        with(CubicCodingDB.getDatabaseInstance().getScoreboardDao()) {

            deleteAll()
            Timber.d("Track, Deleted DB old data")

            insertAll(*scoreItemsForDB.toTypedArray())
            Timber.d("Track, Inserted DB new data")

        }

        val tournament = simulatedResponse.tournamentId
        ScoreboardMetadata.lastNetworkUpdate =
            System.currentTimeMillis()//Update the last time that we successfully returned values...
        ScoreboardMetadata.lastActiveTournament =
            tournament ?: ""//Update the last time that we successfully returned values...
        return ScoreRepository.ScoreRepositoryResult(tournament ?: "", scoreboardItems)
    }

    @WorkerThread
    fun getTestQuestion(uuid: String, callback: GenericRequestListener<String, Throwable>) {
        try {

            val questions = CubicCodingDB.getDatabaseInstance().getQuestionDao().getQuestion(uuid)
            if (questions != null) {
                callback.onResult(questions.testUuid)
                Timber.e("Track, Question found in local db...")
                return
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
                CubicCodingDB.getDatabaseInstance().getQuestionDao().insert(QuestionEntity(testQuestions.uuid ?: "", testQuestions.label, testQuestions.questionTitle, optionsString, answersString, "", testQuestions.maxScore))

                callback.onResult(testQuestions.uuid)
            } else {
                throw CubicCodingRequestException("GetQuestion request not successful", RequestErrorType.UNSUCCESS, response.code())
            }
        } catch (e: Exception) {
            Timber.e(e, "ERROR")
            if (e is CubicCodingRequestException) {
                callback.onFail(e)
            } else {//Turn it into a CubicCodingRequestException
                callback.onFail(CubicCodingRequestException("GetQuestion unknown error", RequestErrorType.GENERIC))
            }
        }
    }

    @WorkerThread
    fun uploadAnswer(testUuid: String, answer: List<Int>): UploadAnswerResult {
        try {
            val response = RequestsManager.cubicCodingManagerApi.uploadAnswer(UploadAnswerRequestPayload(testUuid, answer)).execute()
            return when {
                response.isSuccessful -> {
                    UploadAnswerResult(UploadAnswerStatus.SUCCESS, response.code())
                }
                else -> {
                    val error = CubicCodingRequestException("UploadAnswer request not successful", RequestErrorType.UNSUCCESS, response.code())
                    Timber.e(error, "ERROR")
                    when (response.code()) {
                        HTTP_GONE, HTTP_UNAUTHORIZED, HTTP_CONFLICT -> UploadAnswerResult(UploadAnswerStatus.CANCEL_TASK, response.code())//These codes are specified in the API documentation as invalid states that we won't be able to recover from, hence cancel task...
                        else -> UploadAnswerResult(UploadAnswerStatus.REQUIRES_RETRY, response.code()) //If we didn't get success for any other reason, then retry...
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "ERROR")
            return UploadAnswerResult(UploadAnswerStatus.REQUIRES_RETRY, -1)
        }
    }
    enum class UploadAnswerStatus {
        SUCCESS,
        REQUIRES_RETRY,
        CANCEL_TASK
    }
    data class UploadAnswerResult(val status: UploadAnswerStatus, val code: Int)
}
