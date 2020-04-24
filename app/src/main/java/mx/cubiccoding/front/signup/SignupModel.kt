package mx.cubiccoding.front.signup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.mvp.BaseMVPModel
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.calls.VoucherRequest
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.networking.calls.UserRequest
import mx.cubiccoding.persistence.preferences.UserPersistedData
import okhttp3.ResponseBody

class SignupModel: BaseMVPModel() {

    fun verifyVoucher(uuid: String, callback: GenericRequestListener<GetVoucherResponsePayload, Throwable>) {
        launch(Dispatchers.IO) { VoucherRequest.verifyVoucherIsValid(uuid, callback) }
    }

    fun signup(username: String, password: String, callback: GenericRequestListener<ResponseBody, Throwable>) {
        launch(Dispatchers.IO) {
            val email = UserPersistedData.email//Get the email off the ui thread...
            if (email.isNotEmpty()) {
                UserRequest.signup(email, username, password, callback)
            } else {
                callback.onFail(CubicCodingRequestException("Email is empty.", RequestErrorType.REGISTER_INTERNAL_EMAIL_IS_EMPTY)
                )
            }
        }
    }
}
