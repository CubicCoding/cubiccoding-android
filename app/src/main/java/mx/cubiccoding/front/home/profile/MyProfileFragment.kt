package mx.cubiccoding.front.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.profile_fragment_1.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.cubiccoding.R
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.front.home.scoreboard.model.ScoreRepository
import mx.cubiccoding.front.utils.views.TransitionToScreenAnimations
import mx.cubiccoding.front.utils.views.getBadgeResourceIdByRank
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.model.dtos.ScoreboardItemPayload
import mx.cubiccoding.persistence.preferences.UserPersistedData
import timber.log.Timber

class MyProfileFragment: Fragment() {

    private val profileTransitionAnimations by lazy { TransitionToScreenAnimations() }
    companion object {

        const val TAG = "MyProfileFragment"
        fun newInstance(): MyProfileFragment{
            val fragment = MyProfileFragment()
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
        } else {
            inflater.inflate(R.layout.profile_fragment_2, container, false)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()

        //Is very important for the life cycle that animations start at the end of all the setup...
        if (CubicCodingApplication.instance.shouldAnimateProfile) {
            startIntroAnimation()
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
        specialityValue.text = UserPersistedData.courseName
        classroomValue.text = UserPersistedData.classroomName
        handleRankingViewSetup()

    }

    private fun handleRankingViewSetup() {
        lifecycleScope.launch(Dispatchers.Main) {

            val ranking = getRanking()
            val rank = ranking?.rank ?: -1
            Timber.d("Track, User Ranking: $ranking")
            if (rank > 0) {
                val badgeResourceId = getBadgeResourceIdByRank(rank)
                badge.setImageResource(badgeResourceId)
                rankValue.text = "#$rank"
                rankLabel.text = getString(R.string.rank)
                scoreValue.text = "${ranking?.currentScore?.toInt() ?: 0}/${ranking?.totalScore ?: 0}"
                scoreLabel.text = getString(R.string.score)
                val delayOfRankingViewAnimations = if(CubicCodingApplication.instance.shouldAnimateProfile) {
                    (INIT_DELAY_LARGE_LOGO_IN_MS + INIT_TRANSITION_DURATION_IN_MS + RANKING_ANIMATION_EXTRA_DELAY)
                } else {
                    RANKING_SHOW_QUICK_DELAY
                }

                Timber.d("Track, TIME TO WAIT!!! $delayOfRankingViewAnimations")
                //Start animation to show ranking...
                profileTransitionAnimations.animateVisibilityOfViews(
                    root = profileRoot,
                    visibility = View.VISIBLE,
                    delayInMS = delayOfRankingViewAnimations,//Total wait of first animation + padding time
                    transitionDuration = INIT_TRANSITION_DURATION_IN_MS,
                    viewIds = *intArrayOf(R.id.badge, R.id.rankLabel, R.id.rankValue, R.id.scoreLabel, R.id.scoreValue))
            } else {
                rankValue.text = ""
                rankLabel.text = ""
            }
            CubicCodingApplication.instance.shouldAnimateProfile = false//Prevent future animations until application is killed...
        }
    }

    private suspend fun getRanking() = withContext(Dispatchers.IO) {
        val currentLoggedInEmail = UserPersistedData.email
        val currentLoggedInClassroom = UserPersistedData.classroomName
        val response = ScoreRepository.getScores(currentLoggedInEmail, currentLoggedInClassroom, false)
        if (response != null) {
            val score = response.score
            var ranking: Ranking? = null
            for (item in score) {
                val dataItem: ScoreboardItemPayload = item.getData()
                if (dataItem.email == currentLoggedInEmail) {
                    ranking = Ranking(dataItem.rank, dataItem.currentScore, dataItem.totalOfferedScore)
                    break
                }
            }
            //Log this case where we ended up without a ranking for the user
            if (ranking == null) {
                Timber.e("If there's a tournament active, this call must not be null...")
            }

            ranking
        } else {
            null
        }
    }

    private class Ranking(val rank: Int?, val currentScore: Float?, val totalScore: Int?)
}
