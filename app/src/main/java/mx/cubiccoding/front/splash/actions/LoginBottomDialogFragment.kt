package mx.cubiccoding.front.splash.actions

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.login_bottom_sheet_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.components.login.LoginModelComponent
import mx.cubiccoding.front.components.login.LoginViewContract
import mx.cubiccoding.front.signup.SignupPresenter
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.front.utils.views.showFancyToast

class LoginBottomDialogFragment: BottomSheetDialogFragment(), LoginViewContract {

    private var loginComponent: LoginModelComponent? = null
    private var progressDialog: ProgressActionDialog? = null

    companion object {
        const val TAG = "login.bottom.dialog.fragment"
        fun newInstance() = LoginBottomDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_bottom_sheet_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loginComponent = LoginModelComponent(lifecycleScope)

        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loginComponent?.releaseScope()//Required since lifecycles's scope was passed to login component
    }

    private fun setupViews() {
        loginButton.setOnClickListener {
            startLogin()
        }
        ccPassword.setOnEditorActionListener {
            _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                startLogin()
            }
            false
        }
    }

    private fun startLogin() {
        val username = ccUsername.text.toString()
        val password = ccPassword.text.toString()

        //First check if passwords match...
        val inputError = when {
            username.isEmpty() -> SignupPresenter.RegistrationInputError.EMPTY_USERNAME
            password.isEmpty() -> SignupPresenter.RegistrationInputError.EMPTY_PASSWORD
            else -> SignupPresenter.RegistrationInputError.NO_ERROR
        }

        if (inputError == SignupPresenter.RegistrationInputError.NO_ERROR) {
            loginComponent?.login(this, username, password)
            if (progressDialog == null) {
                progressDialog = ProgressActionDialog(requireContext(), getString(R.string.logging_in), R.drawable.ic_info)
            }
            progressDialog?.show()
        } else {
            when(inputError) {
                SignupPresenter.RegistrationInputError.EMPTY_USERNAME -> showFancyToast(context, getString(R.string.insert_username))
                SignupPresenter.RegistrationInputError.EMPTY_PASSWORD -> showFancyToast(context, getString(R.string.insert_password))
            }
        }
    }

    override fun loginSuccess() {
        showFancyToast(context, getString(R.string.you_logged_in), Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        progressDialog?.dismiss()
        IntentUtils.launchHomeActivity(activity)
        dismissAllowingStateLoss()
    }

    override fun loginFailed() {
        progressDialog?.setErrorMessage(getString(R.string.user_or_password_incorrect))
        //Reset focus to force keyboard to showup...
        ccUsername.clearFocus()
        ccPassword.clearFocus()
    }

}
