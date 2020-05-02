package mx.cubiccoding.front.components.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.cubiccoding.model.dtos.LoginResponsePayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.networking.calls.UserRequest
import timber.log.Timber

class LoginModelComponent(private var scope: CoroutineScope?) {

    fun login(viewContract: LoginViewContract, username: String, password: String) {
        val callback = GenericLeakAndUISafeRequestListener(viewContract, this::onLoginSuccess, this::onError)
        scope?.launch(Dispatchers.IO) {
            try {
                callback.onResult(UserRequest.login(username, password)!!)
            } catch (e: Exception) {
                Timber.e(e, "ERROR")
                if (e is CubicCodingRequestException) {
                    callback.onFail(e)
                } else {//Turn it into a CubicCodingRequestException
                    callback.onFail(CubicCodingRequestException("Logging In unknown error", RequestErrorType.GENERIC))
                }
            }
        }
    }

    fun releaseScope() {
        scope = null
    }

    private fun onLoginSuccess(viewContract: LoginViewContract, response: LoginResponsePayload) {
        viewContract.loginSuccess()
    }

    private fun onError(viewContract: LoginViewContract, error: Throwable) {
        viewContract.loginFailed()
    }

}
