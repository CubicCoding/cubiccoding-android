package mx.cubiccoding.front.home.scoreboard.actions.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kotlinx.android.synthetic.main.student_scoreboard_fragment.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview.ScoreboardSummaryDataItem
import mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview.StudentScoreboardSummaryAdapter
import mx.cubiccoding.front.utils.views.getBadgeResourceIdByRank
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.persistence.preferences.ScoreboardMetadata
import timber.log.Timber
import java.lang.IllegalStateException

class StudentScoreboardFragment: Fragment() {

    companion object {
        const val TAG = "StudentScoreboardFragment"
        const val EMAIL_EXTRA_KEY = "email.extra.key"
        const val AVATAR_EXTRA_KEY = "avatar.extra.key"
        const val DISPLAY_NAME_EXTRA_KEY = "display.name.extra.key"
        const val RANK_EXTRA_KEY = "rank.extra.key"
        const val TOTAL_SCORE_EXTRA_KEY = "max.score.extra.key"
        const val CURRENT_SCORE_EXTRA_KEY = "current.score.extra.key"
        const val MULTI_OPTIONS_POSITION = 0
        const val CHALLENGE_POSITION = 1
        const val BONUS_POSITION = 2

        private var email: String? = null
        private val adapter by lazy { StudentScoreboardSummaryAdapter() }

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

    override fun onDestroyView() {
        super.onDestroyView()
        adapter.clearData()
    }

    private fun setupViews() {

        Timber.e("Track, StudentScoreboardFragment email: $email")

        closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }

        val model: StudentScoreboardViewModel by viewModels { StudentViewModelFactory(email ?: "", ScoreboardMetadata.lastActiveTournamentId) }
        model.isLoading.observe(viewLifecycleOwner, Observer {
            progress.visibility = if (it) View.VISIBLE else View.GONE
        })

        model.getSummaryLiveData().observe(viewLifecycleOwner, Observer {
            onSummaryDataChanged(it)
        })

        //Start getting the summary
        model.getSummaryFromUser()

        setupScreenDefaultInitState()
    }

    private fun onSummaryDataChanged(summaryData: StudentScoreboardViewModel.ScoreboardSummaryModelResult?) {
        //Set initial data in the adapter based on the currently selected tab...
        summaryData?.apply {
            adapter.setData(getItemsByTabPosition(tabLayout.selectedTabPosition, summaryData))
            if (adapter.itemCount < 1) {
                showFancyToast(context, getString(R.string.no_elements_to_display))
            }
        }

        //Setup the tabs listener to update to another set of internal data if required...
        tabLayout.clearOnTabSelectedListeners()
        tabLayout.addOnTabSelectedListener(object: OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                summaryData?.apply {
                    adapter.setData(getItemsByTabPosition(tab?.position ?: 0, summaryData))
                    if (adapter.itemCount < 1) {
                        showFancyToast(context, getString(R.string.no_elements_to_display))
                    }
                }
            }
        })
    }

    private fun setupScreenDefaultInitState() {
        studentSummaryRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        studentSummaryRecyclerView.adapter = adapter
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
    }

    private fun getItemsByTabPosition(tabPosition: Int, summaryData: StudentScoreboardViewModel.ScoreboardSummaryModelResult): List<ScoreboardSummaryDataItem> {
        return when (tabPosition) {
            MULTI_OPTIONS_POSITION -> summaryData.multipleOptions
            CHALLENGE_POSITION -> summaryData.challenges
            BONUS_POSITION -> summaryData.bonusPoints
            else -> throw IllegalStateException("This position is invalid, verify the number of tabs")
        }
    }

}
