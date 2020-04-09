package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.dtos.GetVoucherPayload
import timber.log.Timber

object VoucherRequest {

    @WorkerThread
    fun verifyVoucherIsValid(uuid: String, callback: GenericRequestListener<GetVoucherPayload, Throwable>) {
        try {
            val response = RequestsManager.cubicCodingManagerApi.getVoucher(uuid).execute()
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        if (response.body()?.email?.isNotEmpty() == true) {
                            callback.onResult(response.body())
                        } else {
                            throw CubicCodingRequestException("SendInfoAboutUs body is null", RequestErrorType.VOUCHER_EMAIL_IS_EMPTY, response.code())
                        }
                    } else {
                        throw CubicCodingRequestException("SendInfoAboutUs body is null", RequestErrorType.NULL_BODY, response.code())
                    }
                }
                !response.isSuccessful -> throw CubicCodingRequestException("SendInfoAboutUs request failed", RequestErrorType.UNSUCCESS, response.code())
                else -> throw CubicCodingRequestException("SendInfoAboutUs unknown error", RequestErrorType.GENERIC)
            }
        } catch (e: Exception) {
            Timber.e(e, "ERROR")
            callback.onFail(e)
        }
    }
}
