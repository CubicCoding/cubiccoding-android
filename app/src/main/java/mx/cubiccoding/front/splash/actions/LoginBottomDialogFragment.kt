package mx.cubiccoding.front.splash.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mx.cubiccoding.R
import kotlinx.android.synthetic.main.login_bottom_sheet_dialog.loginButton

class LoginBottomDialogFragment: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "login.bottom.dialog.fragment"
        fun newInstance() = LoginBottomDialogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_bottom_sheet_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        loginButton.setOnClickListener {
            Toast.makeText(context, "Hit Login API!", Toast.LENGTH_SHORT).show()
        }
    }
}
