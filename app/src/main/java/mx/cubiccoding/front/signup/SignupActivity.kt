package mx.cubiccoding.front.signup

import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup_frame_1.ccPassword
import kotlinx.android.synthetic.main.activity_signup_frame_1.ccRetryPassword
import kotlinx.android.synthetic.main.activity_signup_frame_1.ccUsername
import kotlinx.android.synthetic.main.activity_signup_frame_1.signMeUpButton
import kotlinx.android.synthetic.main.activity_splash_frame_1.splashRoot
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.front.utils.views.TransitionToScreenAnimations
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.utils.getErrorMessageForRegistration
import mx.cubiccoding.model.utils.getErrorMessageForVoucherVerification
import mx.cubiccoding.front.signup.SignupPresenter.RegistrationInputError
import mx.cubiccoding.front.utils.IntentUtils
import timber.log.Timber
import java.lang.IllegalStateException


class SignupActivity : AppCompatActivity(), SignupViewContract {

    companion object {
        const val DELAY_LARGE_LOGO_IN_MS = 850L
        const val TRANSITION_DURATION_IN_MS = 350L
    }

    private val presenter by lazy { SignupPresenter() }
    private var verifyVoucherDialog: ProgressActionDialog? = null
    private var registerDialog: ProgressActionDialog? = null

    private val voucherTransitionAnimations by lazy { TransitionToScreenAnimations() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.init(this, SignupModel())

        if (savedInstanceState == null) {
            setContentView(R.layout.activity_signup_frame_1)
            startIntroAnimation()
        } else {
            setContentView(R.layout.activity_signup_frame_2)
        }
        presenter.presentDeeplinks(intent)
        setupViews()
    }

    private fun setupViews() {
        signMeUpButton.setOnClickListener(presenter::presentRegister)
        ccRetryPassword.setOnEditorActionListener(presenter::presentConfirmOkAction)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.terminate()
    }

    private fun startIntroAnimation() {
        voucherTransitionAnimations.transitionScreen(splashRoot, R.layout.activity_signup_frame_2,
            DELAY_LARGE_LOGO_IN_MS,
            TRANSITION_DURATION_IN_MS
        )
    }

    /**
     * ================================================
     * ================================================
     * ================= VIEW CONTRACT ================
     * ================================================
     * ================================================
     */
    override fun showVerifyingVoucher() {
        if (verifyVoucherDialog == null) {
            verifyVoucherDialog = ProgressActionDialog(this, getString(R.string.validating), R.drawable.ic_verified)
        }
        verifyVoucherDialog?.setCancelable(false)
        verifyVoucherDialog?.show()
    }

    override fun voucherVerificationSuccess(response: GetVoucherResponsePayload) {
        verifyVoucherDialog?.dismiss(true)
    }

    override fun voucherVerificationFailed(error: Throwable) {
        verifyVoucherDialog?.setCancelable(true)
        verifyVoucherDialog?.setOnCancelListener {
            finish()
        }
        verifyVoucherDialog?.setErrorMessage(getErrorMessageForVoucherVerification(this, error))
    }

    override fun getRegistrationInput(): SignupPresenter.RegistrationInput {
        return SignupPresenter.RegistrationInput(
            ccUsername.text.toString(),
            ccPassword.text.toString(),
            ccRetryPassword.text.toString())
    }

    override fun formValidationFailed(registerInputError: RegistrationInputError) {
        val (message, focusId) = when(registerInputError) {
            RegistrationInputError.EMPTY_USERNAME -> Pair(getString(R.string.insert_username), R.id.ccUsername)
            RegistrationInputError.SHORT_USERNAME -> Pair(getString(R.string.username_too_short), R.id.ccUsername)
            RegistrationInputError.EMPTY_PASSWORD -> Pair(getString(R.string.insert_password), R.id.ccPassword)
            RegistrationInputError.SHORT_PASSWORD -> Pair(getString(R.string.password_too_short), R.id.ccPassword)
            RegistrationInputError.CONFIRM_PASSWORD -> Pair(getString(R.string.insert_confirm), R.id.ccRetryPassword)
            RegistrationInputError.DIFFERENT_PASSWORDS -> Pair(getString(R.string.error_in_passwords), R.id.ccPassword)
            else -> throw IllegalStateException("Unhandled registration input error, check the logic in SignupPresenter...")
        }

        showFancyToast(this, message, Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        findViewById<EditText>(focusId).requestFocus()
    }

    override fun showRegistering() {
        if (registerDialog == null) {
            registerDialog = ProgressActionDialog(this, getString(R.string.registering), R.drawable.ic_user)
        } else {//Reuse dialog...
            registerDialog?.updateTitle(getString(R.string.registering))
            registerDialog?.updateIcon(R.drawable.ic_user)
        }
        registerDialog?.setCancelable(false)
        registerDialog?.show()
    }

    override fun registerSuccess() {
        registerDialog?.updateTitle(getString(R.string.logging_in))

        presenter.presentLogin()
    }

    override fun registerFailed(error: Throwable) {
        val message = when(error) {
            is CubicCodingRequestException -> getErrorMessageForRegistration(this, error)
            else -> getString(R.string.error_during_register)
        }
        registerDialog?.setErrorMessage(message)
        registerDialog?.setCancelable(true)
    }

    override fun loginSuccess() {
        registerDialog?.dismiss()
        showFancyToast(this, getString(R.string.you_logged_in))

        IntentUtils.launchHomeActivity(this)
        finish()
    }

    override fun loginFailed() {
        registerDialog?.dismiss()
        showFancyToast(this, getString(R.string.error_login_in_try_again))
    }
}
