package mx.cubiccoding.front.home.timeline.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.cubiccoding.R
import mx.cubiccoding.model.dtos.TimelineStepPayload

class TimelineAdapter: RecyclerView.Adapter<TimelineViewHolder>() {

    private val data = mutableListOf<TimelineStepPayload>()
    private var currentProgress = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timeline_step_item, parent, false)
        return TimelineViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        holder.bind(position, data[position], currentProgress)
    }

    fun setTimelineData(steps: List<TimelineStepPayload>, currentProgress: Int) {
        data.clear()
        data.addAll(steps)
        this.currentProgress = currentProgress

        notifyDataSetChanged()
    }
}
