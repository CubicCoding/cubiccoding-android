package mx.cubiccoding.front.utils.views

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Window
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.confirm_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.getCachedColor
import timber.log.Timber

class ConfirmActionDialog(context: Context, @DrawableRes val iconId: Int = R.drawable.ic_cc_no_bg): Dialog(context) {

    companion object {
        const val DELAY_DISMISS_IN_MS = 1000L
    }

    private var tmpContent: String? = null
    private var tmpConfirmListener: ((Dialog) -> Unit)? = null
    private var tmpConfirmLabel: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.confirm_dialog)
        updateIcon(iconId)

        if (tmpConfirmLabel != null && tmpConfirmListener != null) {
            confirmationButton.text = tmpConfirmLabel
            confirmationButton.setOnClickListener {
                tmpConfirmListener?.let { it -> it(this) }
            }
        }
        if (tmpContent != null) {
            alertDialogContent.text = tmpContent
        }
    }

    fun setConfirmationButtonLabel(label: String, code: (Dialog) -> Unit): ConfirmActionDialog {
        if (confirmationButton != null) {
            confirmationButton.text = label
            confirmationButton.setOnClickListener {
                code(this@ConfirmActionDialog)
            }
        } else {
            tmpConfirmLabel = label
            tmpConfirmListener = code
        }
        return this
    }

    fun setContentMessage(content: String): ConfirmActionDialog {
        if (alertDialogContent != null) {
            alertDialogContent.text = content
        } else {
            tmpContent = content
        }
        return this
    }

    fun dismiss(delay: Boolean) {
        if (delay) {
            window?.decorView?.postDelayed({
                //Always delay the dismiss a bit to prevent ugly blinks...
                super.dismiss()
            }, DELAY_DISMISS_IN_MS)
        } else {
            dismiss()
        }
    }

    fun updateIcon(@DrawableRes iconId: Int) {
        confirmDialogIcon.setImageResource(iconId)
        with(confirmDialogIcon.drawable.mutate()) {
            setTintMode(PorterDuff.Mode.SRC_IN)
            setTint(getCachedColor(R.color.colorAccent))
        }
    }
}
