package mx.cubiccoding.model.networking

import java.lang.Exception

enum class RequestErrorType {
    //Basic Http Related Errors
    UNSUCCESS,
    NULL_BODY,
    GENERIC,

    //Api specific errors
    EMAIL_API_SUCCESS_NOT_1,
    VOUCHER_EMAIL_IS_EMPTY,
    REGISTER_INTERNAL_EMAIL_IS_EMPTY,
    QUESTION_ALREADY_ANSWERED
}
class CubicCodingRequestException(message: String, val errorType: RequestErrorType, val httpStatusCode: Int = -1): Exception("Type: $errorType message: $message")