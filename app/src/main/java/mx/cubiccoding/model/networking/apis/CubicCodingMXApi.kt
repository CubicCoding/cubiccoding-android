package mx.cubiccoding.model.networking.apis

import mx.cubiccoding.model.dtos.BasicResponsePayload
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CubicCodingMXApi {

    @FormUrlEncoded
    @POST("/mailer/info_about_us_mailer.php")
    fun infoAboutUs(@Field("email") id: String): Call<BasicResponsePayload>
}
