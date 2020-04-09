package mx.cubiccoding.front.home

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_frame_1.*
import mx.cubiccoding.R
import mx.cubiccoding.front.splash.SplashNetworkActions.NetworkApp
import mx.cubiccoding.front.splash.actions.info.InfoBottomDialogFragment
import mx.cubiccoding.front.splash.actions.LoginBottomDialogFragment
import mx.cubiccoding.front.splash.actions.voucher.VoucherBottomDialogFragment


class Home : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_frame_1)
    }
}
