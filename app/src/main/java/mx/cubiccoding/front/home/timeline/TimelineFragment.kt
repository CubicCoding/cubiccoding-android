package mx.cubiccoding.front.home.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.timeline_fragment.*
import kotlinx.android.synthetic.main.timeline_step_item.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.model.ScoreboardViewModel
import mx.cubiccoding.front.home.timeline.model.TimelineRepository
import mx.cubiccoding.front.home.timeline.model.TimelineViewModel
import mx.cubiccoding.front.home.timeline.recyclerview.TimelineAdapter
import mx.cubiccoding.persistence.preferences.UserPersistedData
import timber.log.Timber


class TimelineFragment: Fragment() {

    companion object {
        const val TAG = "TimelineFragment"
        fun newInstance(): TimelineFragment {
            val fragment = TimelineFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    private var model: TimelineViewModel? = null
    private val adapter = TimelineAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timeline_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {

        //Setup recyclerview...
        timelineRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        timelineRecyclerView.adapter = adapter
        swipeRefreshLayout.setOnRefreshListener {
            model?.loadTimeline(UserPersistedData.classroomName, true)
        }

        val model: TimelineViewModel by viewModels()
        this.model = model
        model.getTimeline().observe(viewLifecycleOwner, Observer {  timelineInfo ->
            handleTimelineObserver(timelineInfo)
        })
        model.getProgressState().observe(viewLifecycleOwner, Observer { inProgress ->
            swipeRefreshLayout.isRefreshing = if (inProgress) {
                errorMessage.visibility = View.GONE
                true
            } else false
        })
    }

    private fun handleTimelineObserver(timelineInfo: TimelineRepository.TimelineInfo?) {
        if (timelineInfo != null) {
            activity?.runOnUiThread {
                swipeRefreshLayout.visibility = View.VISIBLE
                adapter.setTimelineData(timelineInfo.timeline, timelineInfo.currentProgress)
            }
        } else {
            swipeRefreshLayout.visibility = View.GONE
            errorMessage.visibility = View.VISIBLE
        }
    }
}
