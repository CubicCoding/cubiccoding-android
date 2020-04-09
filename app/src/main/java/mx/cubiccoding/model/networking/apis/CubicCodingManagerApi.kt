package mx.cubiccoding.model.networking.apis

import mx.cubiccoding.model.dtos.GetVoucherPayload
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CubicCodingManagerApi {

    @GET("/api/voucher/{uuid}")
    fun getVoucher(@Path("uuid") uuid: String): Call<GetVoucherPayload>
}
