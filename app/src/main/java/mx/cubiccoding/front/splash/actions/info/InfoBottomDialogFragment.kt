package mx.cubiccoding.front.splash.actions.info

import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.info_bottom_sheet_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.dtos.BasicResponsePayload

class InfoBottomDialogFragment : BottomSheetDialogFragment(){

    private val model by lazy { InfoBottomModel() }
    private var progressDialog: ProgressActionDialog? = null

    companion object {
        const val TAG = "info.bottom.dialog.fragment"
        fun newInstance() = InfoBottomDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        model.init()
        return inflater.inflate(R.layout.info_bottom_sheet_dialog, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        model.terminate()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        ccTellMeMoreEmail.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) {
                  sendEmail()
            }
            false
        }

        tellMeMoreSendButton.setOnClickListener {
            sendEmail()
        }
    }

    private fun sendEmail() {
        val email = ccTellMeMoreEmail.text.toString()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {//Invalid email, just return and leave a message...
            showFancyToast(context, getString(R.string.invalid_email), Gravity.CENTER)
            return
        }

        //Fire the request and wait for the response...
        val senderCallbackStub = SenderCallbackStub()
        model.sendEmailForInfoAboutUs(email, GenericLeakAndUISafeRequestListener(this, senderCallbackStub::onSuccess, senderCallbackStub::onFailed))
        tellMeMoreSendButton.text = getString(R.string.sending)
        val context = context ?: return
        if (progressDialog == null) {
            progressDialog = ProgressActionDialog(context, getString(R.string.sending_email), R.drawable.ic_info)
        }
        progressDialog?.show()
    }

    private fun handleRequestSucceeded(response: BasicResponsePayload) {
        dismiss()
        progressDialog?.dismiss(true)

        showFancyToast(context, getString(R.string.email_sent), Gravity.CENTER)
    }

    private fun handleRequestFailed(error: Throwable) {
        tellMeMoreSendButton.text = getString(R.string.send)
        progressDialog?.setErrorMessage(getString(R.string.error_sending_email))
    }

    private class SenderCallbackStub {

        fun onSuccess(view: InfoBottomDialogFragment, response: BasicResponsePayload){
            view.handleRequestSucceeded(response)
        }

        fun onFailed(view: InfoBottomDialogFragment, error: Throwable) {
            view.handleRequestFailed(error)
        }

    }
}
