package mx.cubiccoding.front.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.profile_fragment_1.avatar
import kotlinx.android.synthetic.main.profile_fragment_1.badge
import kotlinx.android.synthetic.main.profile_fragment_1.displayName
import kotlinx.android.synthetic.main.profile_fragment_1.emailValue
import kotlinx.android.synthetic.main.profile_fragment_1.profileRoot
import kotlinx.android.synthetic.main.profile_fragment_1.rankValue
import kotlinx.android.synthetic.main.profile_fragment_1.startedValue
import kotlinx.android.synthetic.main.profile_fragment_1.usernameValue
import mx.cubiccoding.R
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.front.utils.views.TransitionToScreenAnimations
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.persistence.preferences.UserPersistedData

class ProfileFragment: Fragment() {

    private val profileTransitionAnimations by lazy { TransitionToScreenAnimations() }
    companion object {

        const val TAG = "ProfileFragment"
        fun newInstance(): ProfileFragment{
            val fragment = ProfileFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
        const val INIT_DELAY_LARGE_LOGO_IN_MS = 2000L
        const val INIT_TRANSITION_DURATION_IN_MS = 650L
        const val RANKING_ANIMATION_EXTRA_DELAY = 2000L
        const val RANKING_SHOW_QUICK_DELAY = 30L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (CubicCodingApplication.instance.shouldAnimateProfile) {
            inflater.inflate(R.layout.profile_fragment_1, container, false)
        }
        else {
            inflater.inflate(R.layout.profile_fragment_2, container, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()

        //Is very important for the life cycle that animations start at the end of all the setup...
        if (CubicCodingApplication.instance.shouldAnimateProfile) {
            startIntroAnimation()
            CubicCodingApplication.instance.shouldAnimateProfile = false//Prevent future animations until application is killed...
        }
    }

    private fun startIntroAnimation() {
        profileTransitionAnimations.transitionScreen(profileRoot, R.layout.profile_fragment_2,
            INIT_DELAY_LARGE_LOGO_IN_MS,
            INIT_TRANSITION_DURATION_IN_MS
        )
    }

    private fun setupViews() {
        val avatarUrl = UserPersistedData.avatar
        if (avatarUrl.isNotEmpty()) {
            loadImageCircle(url = avatarUrl, imageView = avatar)
        }

        displayName.text = "${UserPersistedData.name} ${UserPersistedData.firstSurname}"
        emailValue.text = UserPersistedData.email
        usernameValue.text = UserPersistedData.username
        startedValue.text = UserPersistedData.createdDate
        handleRankingViewSetup()

    }

    private fun handleRankingViewSetup() {
        //Ranking logic...
        val rank = 1//simulated rank
        rankValue.text = "#$rank"
        if (rank > 0) {
            val badgeResourceId = when(rank) {
                1 -> R.drawable.icon_cc_gold
                2 -> R.drawable.icon_cc_silver
                3 -> R.drawable.icon_cc_bronze
                else -> R.drawable.ic_cc_no_bg
            }
            badge.setImageResource(badgeResourceId)
            rankValue.text = "#$rank"

            val delayOfRankingViewAnimations = if(CubicCodingApplication.instance.shouldAnimateProfile) {
                (INIT_DELAY_LARGE_LOGO_IN_MS + INIT_TRANSITION_DURATION_IN_MS + RANKING_ANIMATION_EXTRA_DELAY)
            } else {
                RANKING_SHOW_QUICK_DELAY
            }

            //Start animation to show ranking...
            profileTransitionAnimations.animateVisibilityOfViews(
                root = profileRoot,
                visibility = View.VISIBLE,
                delayInMS = delayOfRankingViewAnimations,//Total wait of first animation + padding time
                transitionDuration = INIT_TRANSITION_DURATION_IN_MS,
                viewIds = *intArrayOf(R.id.badge, R.id.rankLabel, R.id.rankValue))
        }
    }
}
