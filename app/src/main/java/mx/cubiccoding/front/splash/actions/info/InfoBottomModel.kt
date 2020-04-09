package mx.cubiccoding.front.splash.actions.info

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.mvp.BaseMVPModel
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.calls.AcademyInfoRequest
import mx.cubiccoding.model.dtos.BasicResponsePayload

class InfoBottomModel: BaseMVPModel() {

    fun sendEmailForInfoAboutUs(email: String, callback: GenericRequestListener<BasicResponsePayload, Throwable>) {
        launch(Dispatchers.IO) { AcademyInfoRequest.sendInfoAboutUsEmail(email, callback) }
    }
}
