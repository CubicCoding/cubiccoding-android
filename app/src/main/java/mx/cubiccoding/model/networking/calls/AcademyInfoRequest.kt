package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.dtos.BasicResponsePayload

object AcademyInfoRequest {

    @WorkerThread
    fun sendInfoAboutUsEmail(email: String, callback: GenericRequestListener<BasicResponsePayload, Throwable>) {
        try {
            val response = RequestsManager.cubicCodingMXApi.infoAboutUs(email).execute()
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        if(response.body()?.success == 1) {
                            callback.onResult(response.body())
                        } else {
                            throw CubicCodingRequestException("SendInfoAboutUs body is null", RequestErrorType.EMAIL_API_SUCCESS_NOT_1, response.code())
                        }
                    } else {
                        throw CubicCodingRequestException("SendInfoAboutUs body is null", RequestErrorType.NULL_BODY, response.code())
                    }
                }
                !response.isSuccessful -> throw CubicCodingRequestException("SendInfoAboutUs request failed", RequestErrorType.UNSUCCESS, response.code())
                else -> throw CubicCodingRequestException("SendInfoAboutUs unknown error", RequestErrorType.GENERIC)
            }
        } catch (e: Exception) {
            callback.onFail(e)
        }
    }
}
