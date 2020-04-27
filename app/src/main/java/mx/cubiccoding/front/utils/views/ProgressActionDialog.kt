package mx.cubiccoding.front.utils.views

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.progress_dialog.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.getCachedColor

class ProgressActionDialog(context: Context, private val title: String, @DrawableRes val iconId: Int = R.drawable.ic_cc_no_bg): Dialog(context) {

    companion object {
        const val DELAY_DISMISS_IN_MS = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.progress_dialog)
        alertDialogTitle?.text = title
        updateIcon(iconId)
    }

    override fun show() {
        super.show()

        //Make sure that every time we show the dialog, the error message has disappeared...
        findViewById<TextView>(R.id.alertDialogError)?.visibility = View.GONE
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

    fun updateTitle(title: String) {
        alertDialogTitle.text = title
    }

    fun updateIcon(@DrawableRes iconId: Int) {
        progressIcon.setImageResource(iconId)
        with(progressIcon.drawable.mutate()) {
            setTintMode(PorterDuff.Mode.SRC_IN)
            setTint(getCachedColor(R.color.colorAccent))
        }
    }

    fun setErrorMessage(errorMessage: String) {
        findViewById<TextView>(R.id.alertDialogError)?.apply {
            visibility = View.VISIBLE
            text = errorMessage
        }

        findViewById<View>(R.id.progress)?.visibility = View.INVISIBLE
    }
}
