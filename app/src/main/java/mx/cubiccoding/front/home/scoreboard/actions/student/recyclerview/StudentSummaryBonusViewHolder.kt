package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.View
import kotlinx.android.synthetic.main.bonus_summary_item.view.*
import kotlinx.android.synthetic.main.multioptions_summary_item.view.questionDate
import kotlinx.android.synthetic.main.multioptions_summary_item.view.questionTitle
import kotlinx.android.synthetic.main.multioptions_summary_item.view.score
import mx.cubiccoding.model.dtos.BonusPointsPayload
import mx.cubiccoding.model.dtos.MultipleOptionsSummaryPayload
import mx.cubiccoding.model.utils.getDefaultFormattedDateFromServerDate

class StudentSummaryBonusViewHolder(view: View): StudentScoreboardViewHolder(view) {
    override fun bind(item: ScoreboardSummaryDataItem) {
        val data: BonusPointsPayload = item.getData()
        //Set title
        itemView.questionTitle.text = data.title

        //Set data
        itemView.questionDate.text = getDefaultFormattedDateFromServerDate(data.createdDate)

        itemView.reason.text = data.reason

        //Set score
        itemView.score.text = data.score.toString()
    }
}
