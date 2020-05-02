package mx.cubiccoding.front.home.scoreboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.scoreboard_fragment.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment
import mx.cubiccoding.front.home.scoreboard.model.ScoreboardViewModel
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardAdapter
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardDataItem
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.model.utils.getDefaultFormattedDateFromMillis
import mx.cubiccoding.persistence.preferences.ScoreboardMetadata
import mx.cubiccoding.persistence.preferences.UserPersistedData

class ScoreboardFragment: Fragment() {

    private val adapter by lazy { ScoreboardAdapter() }

    companion object {

        const val TAG = "ScoreboardFragment"
        fun newInstance(): ScoreboardFragment{
            val fragment = ScoreboardFragment()
            val args = Bundle()
            //TODO: Add args when necessary...
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scoreboard_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        scoreboardRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        scoreboardRecyclerView.adapter = adapter

        val model: ScoreboardViewModel by viewModels()
        model.getScoresLiveData().observe(viewLifecycleOwner, Observer {  scoreboardDataResponse ->
            handleScoresObserver(scoreboardDataResponse)
        })

        //Start progress
        swipeRefreshLayout.isEnabled = false
        swipeRefreshLayout.setOnRefreshListener {
            model.loadScores(UserPersistedData.email, UserPersistedData.classroomName, true)
            progress.visibility = View.VISIBLE
            emptyScoreText.visibility = View.GONE
        }

        question.setOnClickListener { GetTestBottomDialogFragment.newInstance().show(childFragmentManager, GetTestBottomDialogFragment.TAG) }
    }

    private fun handleScoresObserver(scoreboardDataResponse: ScoreboardRequest.ScoreboardRequestResult?){
        if (scoreboardDataResponse != null) {
            handleScoresListChanged(scoreboardDataResponse.tournament, scoreboardDataResponse.score)
            //TODO: Notify about the source of the data...
        } else {//TODO: Handle error on scoreboard data case...
            emptyScoreText.text = getString(R.string.error_loading_scores)
            emptyScoreText.visibility = View.VISIBLE
            if (adapter.itemCount < 1) {//Only remove the top metadata if we don't have items in the list and failed to fetch(otherwise there could be the local items and we need this metadata)...
                tournament.visibility = View.INVISIBLE
                lastSync.visibility = View.INVISIBLE
                shadow.visibility = View.INVISIBLE
            }
        }
    }

    private fun handleScoresListChanged(tournnament: String, scoreboardItems: List<ScoreboardDataItem>) {
        progress.visibility = View.GONE
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.isRefreshing = false

        if (scoreboardItems.isNotEmpty()) {
            adapter.populateScoreboard(scoreboardItems)
            tournament.text = tournnament
            tournament.visibility = View.VISIBLE
            lastSync.text = getDefaultFormattedDateFromMillis(ScoreboardMetadata.lastNetworkUpdate)
            lastSync.visibility = View.VISIBLE
            shadow.visibility = View.VISIBLE
        } else {
            emptyScoreText.text = getString(R.string.no_scores)
            emptyScoreText.visibility = View.VISIBLE
            tournament.visibility = View.INVISIBLE
            lastSync.visibility = View.INVISIBLE
            shadow.visibility = View.INVISIBLE
        }
    }

}
