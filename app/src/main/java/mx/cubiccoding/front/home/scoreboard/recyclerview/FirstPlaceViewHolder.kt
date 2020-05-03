package mx.cubiccoding.front.home.scoreboard.recyclerview

import android.view.View
import com.donaumorgen.utel.model.pubsub.PubsubEvents
import kotlinx.android.synthetic.main.first_place_scoreboard_item.view.*
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.dtos.ScoreboardItemPayload
import mx.cubiccoding.model.pubsub.Pubsub

class FirstPlaceViewHolder(view: View): ScoreboardViewHolder(view) {

    override fun bind(item: ScoreboardDataItem) {
        val data: ScoreboardItemPayload = item.getData()

        itemView.setOnClickListener {
            Pubsub.INSTANCE.publish(Pubsub.PubsubData(PubsubEvents.LAUNCH_STUDENT_SCOREBOARD_FRAGMENT, data))
        }
        itemView.displayName.text = data.displayName
        itemView.classroomValue.text = "${data.currentScore?.toInt() ?: 0}/${data.totalOfferedScore}"
        loadImageCircle(itemView.context, data.avatarUrl, itemView.avatar)
    }
}