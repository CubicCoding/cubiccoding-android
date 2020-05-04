package mx.cubiccoding.front.home.scoreboard.actions.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.student_scoreboard_fragment.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.views.getBadgeResourceIdByRank
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.persistence.preferences.ScoreboardMetadata
import timber.log.Timber

class StudentScoreboardFragment: Fragment() {

    companion object {
        const val TAG = "StudentScoreboardFragment"
        const val EMAIL_EXTRA_KEY = "email.extra.key"
        const val AVATAR_EXTRA_KEY = "avatar.extra.key"
        const val DISPLAY_NAME_EXTRA_KEY = "display.name.extra.key"
        const val RANK_EXTRA_KEY = "rank.extra.key"
        const val TOTAL_SCORE_EXTRA_KEY = "max.score.extra.key"
        const val CURRENT_SCORE_EXTRA_KEY = "current.score.extra.key"

        private var email: String? = null

        fun newInstance(email: String?, avatar: String?, displayName: String?, rank: Int?, totalScore: Int?, currentScore: Int?): StudentScoreboardFragment {
            val fragment = StudentScoreboardFragment()
            val args = Bundle()
            this.email = email?.apply { args.putString(EMAIL_EXTRA_KEY, this) }
            avatar?.apply { args.putString(AVATAR_EXTRA_KEY, this) }
            displayName?.apply { args.putString(DISPLAY_NAME_EXTRA_KEY, this) }
            rank?.apply { args.putInt(RANK_EXTRA_KEY, this) }
            totalScore?.apply { args.putInt(TOTAL_SCORE_EXTRA_KEY, this) }
            currentScore?.apply { args.putInt(CURRENT_SCORE_EXTRA_KEY, this) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.student_scoreboard_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {

        val model: StudentScoreboardViewModel by viewModels { StudentViewModelFactory(email ?: "", ScoreboardMetadata.lastActiveTournamentId) }

        val avatarUrl = arguments?.getString(AVATAR_EXTRA_KEY)
        val rank = arguments?.getInt(RANK_EXTRA_KEY) ?: 0
        val currentScore = arguments?.getInt(CURRENT_SCORE_EXTRA_KEY) ?: 0
        val totalScore = arguments?.getInt(TOTAL_SCORE_EXTRA_KEY) ?: 0
        val badgeResourceId = getBadgeResourceIdByRank(rank)

        loadImageCircle(url = avatarUrl, imageView = avatar)
        badge.setImageResource(badgeResourceId)

        rankValue.text = "#$rank"
        rankLabel.text = getString(R.string.rank)
        scoreValue.text = "$currentScore/$totalScore"
        scoreLabel.text = getString(R.string.score)

        tabLayout.clearOnTabSelectedListeners()
        tabLayout.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Timber.d("Track, Tab selected: ${tab?.position}")
            }
        })
        tabLayout.isEnabled = false
    }

}
