package mx.cubiccoding.front.signup

import android.content.Intent
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import mx.cubiccoding.front.components.login.LoginModelComponent
import mx.cubiccoding.front.mvp.BaseMVPPresenter
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.utils.Constants
import okhttp3.ResponseBody
import timber.log.Timber

class SignupPresenter : BaseMVPPresenter<SignupViewContract, SignupModel>() {

    private val loginComponent by lazy { LoginModelComponent() }

    fun presentDeeplinks(intent: Intent?) {
        val voucherUuid = intent?.data?.lastPathSegment
        if (voucherUuid?.isNotEmpty() == true && IntentUtils.isNotLaunchedFromHistory(intent)) {//Validate if we have a voucher from deeplink after animation is done...
            Handler().postDelayed({
                val senderCallbackStub = VoucherVerificationCallbackStub()
                model.verifyVoucher(
                    voucherUuid,
                    GenericLeakAndUISafeRequestListener(
                        viewContract,
                        senderCallbackStub::onSuccess,
                        senderCallbackStub::onFailed
                    )
                )
                viewContract.showVerifyingVoucher()
            }, SignupActivity.DELAY_LARGE_LOGO_IN_MS + SignupActivity.TRANSITION_DURATION_IN_MS)
        }
    }

    fun presentConfirmOkAction(view: TextView, action: Int, event: KeyEvent?): Boolean {
        if (action == EditorInfo.IME_ACTION_DONE) { presentRegister(view) }
        return false
    }

    fun presentRegister(view: View) {
        val (username, password, confirm) = viewContract.getRegistrationInput()

        //First check if passwords match...
        val inputError = when {
            username.isEmpty() -> RegistrationInputError.EMPTY_USERNAME
            username.length < Constants.MIN_USERNAME_LENGTH -> RegistrationInputError.SHORT_USERNAME
            password.isEmpty() -> RegistrationInputError.EMPTY_PASSWORD
            password.length < Constants.MIN_PASSWORD_LENGTH -> RegistrationInputError.SHORT_PASSWORD
            confirm.isEmpty() -> RegistrationInputError.CONFIRM_PASSWORD
            password != confirm -> RegistrationInputError.DIFFERENT_PASSWORDS
            else -> RegistrationInputError.NO_ERROR
        }

        if (inputError == RegistrationInputError.NO_ERROR) {
            val registerCallbackStub = RegistrationCallbackStub()
            model.signup(
                username,
                password,
                GenericLeakAndUISafeRequestListener(viewContract, registerCallbackStub::onSuccess, registerCallbackStub::onFailed))
            viewContract.showRegistering()
        } else {
            viewContract.formValidationFailed(inputError)
        }
    }

    fun presentLogin() {
        val (username, password) = viewContract.getRegistrationInput()
        loginComponent.login(viewContract, username, password)
    }

    private class VoucherVerificationCallbackStub {
        fun onSuccess(view: SignupViewContract, response: GetVoucherResponsePayload) {
            view.voucherVerificationSuccess(response)
        }

        fun onFailed(view: SignupViewContract, error: Throwable) {
            view.voucherVerificationFailed(error)
        }
    }

    private class RegistrationCallbackStub {
        fun onSuccess(view: SignupViewContract, response: ResponseBody) {
            view.registerSuccess()
        }

        fun onFailed(view: SignupViewContract, error: Throwable) {
            view.registerFailed(error)
            Timber.e(error, "Error during signup")
        }
    }

    enum class RegistrationInputError {
        EMPTY_USERNAME,
        SHORT_USERNAME,
        EMPTY_PASSWORD,
        SHORT_PASSWORD,
        CONFIRM_PASSWORD,
        DIFFERENT_PASSWORDS,
        NO_ERROR

    }

    data class RegistrationInput(val username: String, val password: String, val confirm: String)
}
