package mx.cubiccoding.front.home.scoreboard.recyclerview

import android.view.View
import kotlinx.android.synthetic.main.first_place_scoreboard_item.view.*
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.dtos.ScoreboardItemPayload

class FirstPlaceViewHolder(view: View): ScoreboardViewHolder(view) {

    override fun bind(item: ScoreboardDataItem) {
        itemView.setOnClickListener {
            showFancyToast(itemView.context, "Clicked FIRST PLACE")
        }
        val data: ScoreboardItemPayload = item.getData()
        itemView.displayName.text = data.displayName
        itemView.classroomValue.text = "${data.currentScore?.toInt() ?: 0}/${data.totalOfferedScore}"
        loadImageCircle(itemView.context, data.avatarUrl, itemView.avatar)
    }
}