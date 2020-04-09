package mx.cubiccoding.front.signup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.mvp.BaseMVPModel
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.calls.VoucherRequest
import mx.cubiccoding.model.dtos.GetVoucherPayload

class SignupModel: BaseMVPModel() {

    fun verifyVoucher(uuid: String, callback: GenericRequestListener<GetVoucherPayload, Throwable>) {
        launch(Dispatchers.IO) { VoucherRequest.verifyVoucherIsValid(uuid, callback) }
    }

    fun register(username: String, password: String, callback: GenericRequestListener<GetVoucherPayload, Throwable>) {
        launch(Dispatchers.IO) { VoucherRequest.verifyVoucherIsValid(uuid, callback) }
    }

}