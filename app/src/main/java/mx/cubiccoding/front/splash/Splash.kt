package mx.cubiccoding.front.splash

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_frame_1.*
import mx.cubiccoding.R
import mx.cubiccoding.front.splash.SplashNetworkActions.NetworkApp
import mx.cubiccoding.front.splash.actions.info.InfoBottomDialogFragment
import mx.cubiccoding.front.splash.actions.LoginBottomDialogFragment
import mx.cubiccoding.front.splash.actions.voucher.VoucherBottomDialogFragment
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.front.utils.views.TransitionToScreenAnimations
import mx.cubiccoding.persistence.preferences.UserPersistedData


class Splash : AppCompatActivity() {

    private val splashTransitionAnimations by lazy { TransitionToScreenAnimations() }
    private val networkActions by lazy { SplashNetworkActions() }
    companion object {
        const val DELAY_LARGE_LOGO_IN_MS = 1250L
        const val LOGO_ANIMATE_TO_OPTIONS_DURATION_IN_MS = 400L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (UserPersistedData.isLogged) {//Deeplink into home directly if we are logged...
            IntentUtils.launchHomeActivity(this)
        } else {
            if (savedInstanceState == null) {
                setContentView(R.layout.activity_splash_frame_1)
                startIntroAnimation()
            } else {
                setContentView(R.layout.activity_splash_frame_2)
            }
            setupViews()
        }
    }

    private fun startIntroAnimation() {
        splashTransitionAnimations.transitionScreen(splashRoot, R.layout.activity_splash_frame_2, DELAY_LARGE_LOGO_IN_MS, LOGO_ANIMATE_TO_OPTIONS_DURATION_IN_MS)
    }

    private fun setupViews() {
        //BottomSheet Actions
        loginFab.setOnClickListener { LoginBottomDialogFragment.newInstance().show(supportFragmentManager, LoginBottomDialogFragment.TAG) }
        voucherFab.setOnClickListener { VoucherBottomDialogFragment.newInstance().show(supportFragmentManager, VoucherBottomDialogFragment.TAG) }
        infoFab.setOnClickListener { InfoBottomDialogFragment.newInstance().show(supportFragmentManager, InfoBottomDialogFragment.TAG) }
    }

    fun launchFacebook(view: View) { networkActions.openOpenNetworkApp(this, NetworkApp.FACEBOOK) }
    fun launchTwitter(view: View) { networkActions.openOpenNetworkApp(this, NetworkApp.TWITTER) }
    fun launchInstagram(view: View) { networkActions.openOpenNetworkApp(this, NetworkApp.INSTAGRAM) }
}
