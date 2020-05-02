package mx.cubiccoding.front.home.scoreboard.actions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.get_test_sheet_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.questions.TestActivity
import mx.cubiccoding.front.utils.views.ProgressActionDialog
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.GenericLeakAndUISafeRequestListener
import mx.cubiccoding.model.networking.RequestErrorType

class GetTestBottomDialogFragment : BottomSheetDialogFragment() {

    private val model by lazy { GetTestBottomModel() }
    private var progressDialog: ProgressActionDialog? = null

    companion object {
        const val TAG = "get.test.bottom.dialog.fragment"
        const val TEST_UUID_PRE_POPULATED_KEY = "test.uuid.pre.populated.key"
        fun newInstance() = GetTestBottomDialogFragment()
        fun newInstance(args: Bundle) = GetTestBottomDialogFragment().apply { arguments = args }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        model.init()
        return inflater.inflate(R.layout.get_test_sheet_dialog, container, false)
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
        val prepopulatedUuid = arguments?.getString(TEST_UUID_PRE_POPULATED_KEY) ?: ""
        if (prepopulatedUuid.isNotEmpty()) {//Only prepopulate this value if there is something to prepopulate it with...
            ccTestId.setText(prepopulatedUuid)
        }
        ccTestId.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_DONE) { downloadQuickTest() }
            false
        }
        downloadTestButton.setOnClickListener {
            downloadQuickTest()
        }
    }

    /**
     * Download the test info...
     */
    private fun downloadQuickTest() {
        //Fire the request and wait for the response...
        val uuid = ccTestId.text.toString()
        val senderCallbackStub = GetTestCallbackStub ()
        model.getTestQuestion(uuid, GenericLeakAndUISafeRequestListener(this, senderCallbackStub::onSuccess, senderCallbackStub::onFailed))
        downloadTestButton.text = getString(R.string.downloading)
        val context = context ?: return
        if (progressDialog == null) {
            progressDialog = ProgressActionDialog(context, getString(R.string.downloading_question), R.drawable.ic_question_answer)
        }
        progressDialog?.setCancelable(false)
        progressDialog?.show()
        ccTestId.setText("")
    }

    private fun handleGettingTestSucceeded(uuid: String) {
        progressDialog?.setOnDismissListener {
            val testIntent = Intent(context, TestActivity::class.java)
            testIntent.putExtra(TestActivity.TEST_ID_EXTRA, uuid)
            activity?.startActivity(testIntent)
            dismissAllowingStateLoss()
        }
        progressDialog?.dismiss(true)
    }

    private fun handleGettingTestFailed(error: Throwable) {

        val message = if(error is CubicCodingRequestException && error.errorType == RequestErrorType.QUESTION_ALREADY_ANSWERED) {
            getString(R.string.question_already_answered)
        } else {
            getString(R.string.error_downloading_question)
        }

        progressDialog?.setOnDismissListener {
            dismissAllowingStateLoss()
        }
        context?.apply {
            progressDialog?.setErrorMessage(message)
            progressDialog?.setCancelable(true)
        }
    }

    private class GetTestCallbackStub {
        fun onSuccess(view: GetTestBottomDialogFragment, uuid: String) {
            view.handleGettingTestSucceeded(uuid)
        }

        fun onFailed(view: GetTestBottomDialogFragment, error: Throwable) {
            view.handleGettingTestFailed(error)
        }
    }
}