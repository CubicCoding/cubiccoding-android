package mx.cubiccoding.front.signup

import mx.cubiccoding.model.dtos.GetVoucherPayload

interface SignupViewContract {

    fun showVerifyingVoucher()
    fun voucherVerificationSuccess(response: GetVoucherPayload)
    fun voucherVerificationFailed(e: Throwable)
    fun getRegistrationInput(): SignupPresenter.RegistrationInput
}