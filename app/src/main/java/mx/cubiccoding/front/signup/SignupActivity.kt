package mx.cubiccoding.front.signup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signup_frame_1.*
import kotlinx.android.synthetic.main.activity_splash_frame_1.*
import kotlinx.android.synthetic.main.activity_splash_frame_1.splashRoot
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.front.utils.views.TransitionToScreenAnimations
import mx.cubiccoding.model.dtos.GetVoucherPayload
import mx.cubiccoding.model.utils.getErrorMessageForVoucherVerification


class SignupActivity : AppCompatActivity(), SignupViewContract {

    companion object {
        const val DELAY_LARGE_LOGO_IN_MS = 850L
        const val TRANSITION_DURATION_IN_MS = 350L
    }

    private val presenter by lazy { SignupPresenter() }
    private var progressDialog: ProgressActionDialog? = null

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
        if (progressDialog == null) {
            progressDialog = ProgressActionDialog(this, getString(R.string.validating), R.drawable.ic_verified)
        }
        progressDialog?.setCancelable(false)
        progressDialog?.show()

    }

    override fun voucherVerificationSuccess(response: GetVoucherPayload) {
        progressDialog?.dismiss(true)
    }

    override fun voucherVerificationFailed(error: Throwable) {
        progressDialog?.setCancelable(true)
        progressDialog?.setOnCancelListener {
            finish()
        }
        progressDialog?.setErrorMessage(getErrorMessageForVoucherVerification(this, error))
    }

    override fun getRegistrationInput(): SignupPresenter.RegistrationInput {
        return SignupPresenter.RegistrationInput(
            ccUsername.text.toString(),
            ccPassword.text.toString(),
            ccRetryPassword.text.toString())
    }
}
