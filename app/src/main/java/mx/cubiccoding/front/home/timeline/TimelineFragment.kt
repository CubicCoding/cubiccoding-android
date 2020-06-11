package mx.cubiccoding.front.home.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import mx.cubiccoding.R
import mx.cubiccoding.front.home.timeline.model.TimelineRepository
import mx.cubiccoding.front.home.timeline.model.TimelineViewModel


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timeline_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.model = model
        model?.getTimeline()?.observe(viewLifecycleOwner, Observer {  timelineInfo ->
            handleTimelineObserver(timelineInfo)
        })
    }

    private fun handleTimelineObserver(timelineInfo: TimelineRepository.TimelineInfo) {
        //TODO: Populate recyclerview...
    }
}
