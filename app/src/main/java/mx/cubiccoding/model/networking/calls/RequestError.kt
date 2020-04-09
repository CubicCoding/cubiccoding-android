package mx.cubiccoding.model.networking.calls

import java.lang.Exception

enum class RequestErrorType {
    //Basic Http Related Errors
    UNSUCCESS,
    NULL_BODY,
    GENERIC,

    //Api specific errors
    EMAIL_API_SUCCESS_NOT_1,
    VOUCHER_EMAIL_IS_EMPTY
}
class CubicCodingRequestException(message: String, val errorType: RequestErrorType, val httpStatusCode: Int = -1): Exception("Type: $errorType message: $message")