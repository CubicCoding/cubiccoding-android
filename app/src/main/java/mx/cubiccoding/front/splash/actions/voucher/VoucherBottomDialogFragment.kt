package mx.cubiccoding.front.splash.actions.voucher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.voucher_bottom_sheet_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.signup.SignupActivity
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.dtos.GetVoucherResponsePayload
import mx.cubiccoding.model.utils.getErrorMessageForVoucherVerification

class VoucherBottomDialogFragment : BottomSheetDialogFragment() {

    private val model by lazy { VoucherBottomModel() }
    private var progressDialog: ProgressActionDialog? = null

    companion object {
        const val TAG = "voucher.bottom.dialog.fragment"
        fun newInstance() = VoucherBottomDialogFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.init()
        return inflater.inflate(R.layout.voucher_bottom_sheet_dialog, container, false)
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
        ccVoucher.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { verifyVoucher() }
            false
        }
        uploadVoucherButton.setOnClickListener {
            verifyVoucher()
        }
    }

    /**
     * Uploads voucher id to cubiccoding manager api to see if is valid...
     */
    private fun verifyVoucher() {
        //Fire the request and wait for the response...
        val uuid = ccVoucher.text.toString()
        val senderCallbackStub = VoucherVerificationCallbackStub()
        model.verifyVoucher(
            uuid,
            GenericLeakAndUISafeRequestListener(this, senderCallbackStub::onSuccess, senderCallbackStub::onFailed)
        )
        uploadVoucherButton.text = getString(R.string.validating)
        val context = context ?: return
        if (progressDialog == null) {
            progressDialog = ProgressActionDialog(context, getString(R.string.validating), R.drawable.ic_verified)
        }
        progressDialog?.show()
        ccVoucher.setText("")
    }

    private fun handleGettingVoucherSucceeded() {
        progressDialog?.setOnDismissListener {
            val activity = context as Activity
            activity.startActivity(Intent(context, SignupActivity::class.java))
            activity.overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
            dismissAllowingStateLoss()
        }
        progressDialog?.dismiss(true)
    }

    private fun handleGettingVoucherFailed(error: Throwable) {
        progressDialog?.setOnDismissListener {
            dismissAllowingStateLoss()
        }
        context?.apply {
            progressDialog?.setErrorMessage(getErrorMessageForVoucherVerification(this, error))
        }
    }

    private class VoucherVerificationCallbackStub {
        fun onSuccess(view: VoucherBottomDialogFragment, response: GetVoucherResponsePayload) {
            view.handleGettingVoucherSucceeded()
        }

        fun onFailed(view: VoucherBottomDialogFragment, error: Throwable) {
            view.handleGettingVoucherFailed(error)
        }
    }
}
