package mx.cubiccoding.model.networking.apis

import mx.cubiccoding.model.dtos.*
import mx.cubiccoding.model.networking.RequestsManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CubicCodingManagerApi {

    @GET("/api/vouchers/{uuid}")
    fun getVoucher(@Path("uuid") uuid: String): Call<GetVoucherResponsePayload>

    @POST("/api/students/signup")
    fun signup(@Body requestBody: SignupRequestPayload): Call<ResponseBody>

    @POST("/login?requires_profile=true")
    fun login(@Body requestBody: LoginRequestPayload): Call<LoginResponsePayload>

    @GET("/api/scoreboard/score-tests/{uuid}")
    fun getQuestion(@Path("uuid" ) uuid: String, @HeaderMap headers: Map<String, String> = RequestsManager.getAuthorizationHeader()): Call<GetTestResponsePayload>

    @POST("/api/scoreboard/score-answers")
    fun uploadAnswer(@Body uploadAnswerRequest: UploadAnswerRequestPayload, @HeaderMap headers: Map<String, String> = RequestsManager.getAuthorizationHeader()): Call<ResponseBody>

    @POST("/api/push-notifications")
    fun registerFirebaseToken(@Body uploadAnswerRequest: RegisterFirebaseTokenRequestPayload, @HeaderMap headers: Map<String, String> = RequestsManager.getAuthorizationHeader()): Call<ResponseBody>

    @GET("/api/scoreboard")
    fun getScoreboard(@Query("email") email: String, @Query("classroomName") classroomName: String, @HeaderMap headers: Map<String, String> = RequestsManager.getAuthorizationHeader()): Call<ScoreboardResponsePayload>

    @GET("/api/scoreboard/history")
    fun getScoreboardSummaryForUser(@Query("email") email: String, @Query("tournamentId") tournamentId: Int, @HeaderMap headers: Map<String, String> = RequestsManager.getAuthorizationHeader()): Call<ScoreboardUserSummaryPayload>
}
