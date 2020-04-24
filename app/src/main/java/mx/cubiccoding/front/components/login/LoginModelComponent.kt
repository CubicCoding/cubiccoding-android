package mx.cubiccoding.front.components.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mx.cubiccoding.model.dtos.LoginResponsePayload
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.networking.calls.UserRequest

class LoginModelComponent {

    fun login(viewContract: LoginViewContract, username: String, password: String) {
        val callback = GenericLeakAndUISafeRequestListener(viewContract, this::onLoginSuccess, this::onError)
        GlobalScope.launch(Dispatchers.IO) {
            UserRequest.login(username, password, callback)
        }
    }

    private fun onLoginSuccess(viewContract: LoginViewContract, response: LoginResponsePayload) {
        viewContract.loginSuccess()
    }

    private fun onError(viewContract: LoginViewContract, error: Throwable) {
        viewContract.loginFailed()
    }

}
