package mx.cubiccoding.front.signup

import android.content.Intent
import android.os.Handler
import android.view.Gravity
import android.view.View
import mx.cubiccoding.R
import mx.cubiccoding.front.mvp.BaseMVPPresenter
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.dtos.GetVoucherPayload

class SignupPresenter: BaseMVPPresenter<SignupViewContract, SignupModel>() {

    fun presentDeeplinks(intent: Intent?) {
        val voucherUuid = intent?.data?.lastPathSegment
        if (voucherUuid?.isNotEmpty() == true && IntentUtils.isNotLaunchedFromHistory(intent)) {//Validate if we have a voucher from deeplink after animation is done...
            Handler().postDelayed({
                val senderCallbackStub = VoucherVerificationCallbackStub()
                model.verifyVoucher(voucherUuid, GenericLeakAndUISafeRequestListener(viewContract, senderCallbackStub::onSuccess, senderCallbackStub::onFailed))
                viewContract.showVerifyingVoucher()
            }, SignupActivity.DELAY_LARGE_LOGO_IN_MS + SignupActivity.TRANSITION_DURATION_IN_MS)
        }
    }

    fun presentRegister(view: View) {
        val (username, password, confirm) = viewContract.getRegistrationInput()

        //First check if passwords match...
        if (password == confirm) {
            model.
        } else {
            showFancyToast(
                view.context,
                view.context.getString(R.string.error_in_passwords),
                Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        }
    }

    private class VoucherVerificationCallbackStub {
        fun onSuccess(view: SignupViewContract, response: GetVoucherPayload) {
            view.voucherVerificationSuccess(response)
        }

        fun onFailed(view: SignupViewContract, error: Throwable) {
            view.voucherVerificationFailed(error)
        }
    }

    data class RegistrationInput(val username: String, val password: String, val confirm: String)
}
