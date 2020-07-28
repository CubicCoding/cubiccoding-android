package mx.cubiccoding.front.home.scoreboard.recyclerview

import android.view.View
import com.donaumorgen.utel.model.pubsub.PubsubEvents
import kotlinx.android.synthetic.main.non_first_place_scoreboard_item.view.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.getCachedColor
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.model.dtos.ScoreboardItemPayload
import mx.cubiccoding.model.pubsub.Pubsub
import mx.cubiccoding.persistence.preferences.UserPersistedData

class NonFirstPlaceViewHolder(view: View): ScoreboardViewHolder(view) {

    override fun bind(item: ScoreboardDataItem) {
        val data: ScoreboardItemPayload = item.getData()
        itemView.setOnClickListener {
            Pubsub.INSTANCE.publish(Pubsub.PubsubData(PubsubEvents.LAUNCH_STUDENT_SCOREBOARD_FRAGMENT, data))
        }
        itemView.displayName.text = data.displayName
        itemView.classroomValue.text = "${data.currentScore?.toInt() ?: 0}/${data.totalOfferedScore}"
        loadImageCircle(itemView.context, data.avatarUrl, itemView.avatar)

        //Handle ranking
        val rank = data.rank
        itemView.rankValue.text = rank.toString()
        val (icResource, ringColor, visibility) = when (rank) {
            2 -> Triple(R.drawable.icon_cc_silver, getCachedColor(R.color.silver), View.VISIBLE)
            3 -> Triple(R.drawable.icon_cc_bronze, getCachedColor(R.color.bronze), View.VISIBLE)
            else -> Triple(-1, -1, View.INVISIBLE)
        }

        if (visibility == View.VISIBLE) {
            itemView.badge.setImageResource(icResource)
            itemView.avatarRing.background.setTint(ringColor)
        }

        itemView.badge.visibility = visibility

        //Handle selection
        if (data.email == UserPersistedData.email) {
            itemView.selectionBG.visibility = View.VISIBLE
        } else {
            itemView.selectionBG.visibility = View.GONE
        }
    }
}