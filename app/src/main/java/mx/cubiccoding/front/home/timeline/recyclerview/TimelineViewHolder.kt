package mx.cubiccoding.front.home.timeline.recyclerview

import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.timeline_step.view.*
import mx.cubiccoding.front.utils.getCachedColor
import mx.cubiccoding.model.dtos.TimelineStepPayload
import mx.cubiccoding.model.utils.Constants
import timber.log.Timber
import java.lang.StringBuilder

class TimelineViewHolder(view: View): RecyclerView.ViewHolder(view) {

    fun bind(position: Int, timelineStepPayload: TimelineStepPayload, currentProgress: Int) {

        val colorPoolIndex = position % Constants.POOL_OF_COLORS.size
        val color = getCachedColor(Constants.POOL_OF_COLORS[colorPoolIndex])

        itemView.timelineStep.apply {
            text = (position + 1).toString()
            DrawableCompat.setTint(DrawableCompat.wrap(background), color);
        }
        itemView.stepName.apply {
            text = timelineStepPayload.name
            setTextColor(color)
        }
        itemView.stepDescription.text = timelineStepPayload.description
        itemView.stepTopics.text = getTopicsVerticallyOrdered(timelineStepPayload.topics)
    }

    private fun getTopicsVerticallyOrdered(topics: List<String>): String {
        val topicsBuilder = StringBuilder("Temas:\n")
        topics.forEach {topic -> topicsBuilder.append("- $topic\n") }
        return topicsBuilder.toString()
    }

}