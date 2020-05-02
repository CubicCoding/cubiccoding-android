package mx.cubiccoding.front.home.scoreboard.recyclerview

import android.view.View
import mx.cubiccoding.model.dtos.ScoreboardItemPayload

class HeaderViewHolder(view: View): ScoreboardViewHolder(view) {

    override fun bind(item: ScoreboardDataItem) {
        val data: ScoreboardItemPayload = item.getData()
        //TODO: Finish Binding for headers is scoreboard...
    }
}