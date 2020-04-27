package mx.cubiccoding.front.signup

import mx.cubiccoding.front.components.login.LoginViewContract
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload

interface SignupViewContract: LoginViewContract {

    fun showVerifyingVoucher()
    fun voucherVerificationSuccess(response: GetVoucherResponsePayload)
    fun voucherVerificationFailed(e: Throwable)
    fun registerSuccess()
    fun registerFailed(error: Throwable)
    fun showRegistering()
    fun formValidationFailed(registerInputError: SignupPresenter.RegistrationInputError)
    fun getRegistrationInput(): SignupPresenter.RegistrationInput

}
