package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.persistence.preferences.UserPersistedData
import timber.log.Timber

object VoucherRequest {

    @WorkerThread
    fun verifyVoucherIsValid(uuid: String, callback: GenericRequestListener<GetVoucherResponsePayload, Throwable>) {
        try {
            val response = RequestsManager.cubicCodingManagerApi.getVoucher(uuid).execute()
            when {
                response.isSuccessful -> {
                    if (response.body() != null) {
                        val email = response.body()?.email
                        if (email?.isNotEmpty() == true) {
                            UserPersistedData.email = email//Store the email and link it to the session of this device. (This will be used during registration process)
                            callback.onResult(response.body())
                        } else {
                            throw CubicCodingRequestException(
                                "VerifyVoucherIsValid body is null",
                                RequestErrorType.VOUCHER_EMAIL_IS_EMPTY,
                                response.code()
                            )
                        }
                    } else {
                        throw CubicCodingRequestException(
                            "VerifyVoucherIsValid body is null",
                            RequestErrorType.NULL_BODY,
                            response.code()
                        )
                    }
                }
                !response.isSuccessful -> throw CubicCodingRequestException(
                    "VerifyVoucherIsValid request failed",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "ERROR")
            if (e is CubicCodingRequestException) {
                callback.onFail(e)
            } else {//Turn it into a CubicCodingRequestException
                callback.onFail(CubicCodingRequestException("VerifyVoucherIsValid unknown error", RequestErrorType.GENERIC))
            }
        }
    }
}
