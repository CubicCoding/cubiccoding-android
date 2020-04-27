package mx.cubiccoding.front.splash.actions.voucher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.mvp.BaseMVPModel
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.calls.VoucherRequest
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload

class VoucherBottomModel: BaseMVPModel() {

    fun verifyVoucher(uuid: String, callback: GenericRequestListener<GetVoucherResponsePayload, Throwable>) {
        launch(Dispatchers.IO) { VoucherRequest.verifyVoucherIsValid(uuid, callback) }
    }
}
