package mx.cubiccoding.model.networking.apis

import mx.cubiccoding.model.dtos.*
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
    fun getQuestion(@Path("uuid" ) testUuid: String): Call<GetTestResponsePayload>

    @POST("/api/scoreboard/score-answers")
    fun uploadAnswer(uploadAnswerRequest: UploadAnswerRequestPayload): Call<ResponseBody>

    @POST("/api/notifications/register")
    fun registerFirebaseToken(@Body uploadAnswerRequest: RegisterFirebaseTokenRequestPayload, @HeaderMap headers: Map<String, String>): Call<ResponseBody>
}
