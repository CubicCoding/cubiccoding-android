package mx.cubiccoding.front.home.timeline

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import kotlinx.android.synthetic.main.timeline_fragment.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.timeline.model.TimelineRepository
import mx.cubiccoding.front.home.timeline.model.TimelineViewModel
import mx.cubiccoding.front.home.timeline.recyclerview.TimelineAdapter
import mx.cubiccoding.persistence.preferences.UserPersistedData


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
    private var smoothScroller: SmoothScroller? = null

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

        //Init the scroller
        smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }

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
                val timeline = timelineInfo.timeline
                swipeRefreshLayout.visibility = View.VISIBLE
                adapter.setTimelineData(timeline, timelineInfo.currentProgress)

                //Recyclerview updates logic...
                timelineRecyclerView.apply {
                    val tmpSmoothScroller = smoothScroller
                    if (tmpSmoothScroller != null) {
                        tmpSmoothScroller.targetPosition = timelineInfo.currentProgress
                        (layoutManager as? LinearLayoutManager)?.startSmoothScroll(tmpSmoothScroller)
                    } else {
                        layoutManager?.scrollToPosition(timelineInfo.currentProgress)
                    }
                }

                //Progress updates logic...
                progressBar.max = timeline.size
                progressBar.progress = timeline.size.coerceAtMost(timelineInfo.currentProgress)
//                progressBar.secondaryProgress = 10
            }
        } else {
            swipeRefreshLayout.visibility = View.GONE
            errorMessage.visibility = View.VISIBLE
        }
    }
}
