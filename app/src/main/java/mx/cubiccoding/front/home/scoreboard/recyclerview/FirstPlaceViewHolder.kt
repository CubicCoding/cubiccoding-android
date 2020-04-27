package mx.cubiccoding.front.home.scoreboard.recyclerview

import android.view.View
import kotlinx.android.synthetic.main.first_place_scoreboard_item.view.*
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.dtos.ScoreboardItemPayload

class FirstPlaceViewHolder(view: View): ScoreboardViewHolder(view) {

    override fun bind(item: ScoreboardRecyclerViewItem) {
        itemView.setOnClickListener {
            showFancyToast(itemView.context, "Clicked FIRST PLACE")
        }
        val data: ScoreboardItemPayload = item.getData()
        itemView.displayName.text = "${data.name} ${data.firstSurname}"
        itemView.scoreValue.text = "${data.score}/${data.totalScore}"
        loadImageCircle(itemView.context, data.avatar, itemView.avatar)
    }
}