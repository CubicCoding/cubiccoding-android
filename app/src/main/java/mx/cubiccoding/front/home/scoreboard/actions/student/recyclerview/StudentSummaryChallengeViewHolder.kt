package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.challenges_summary_item.view.*
import kotlinx.android.synthetic.main.multioptions_summary_item.view.*
import kotlinx.android.synthetic.main.multioptions_summary_item.view.score
import mx.cubiccoding.model.dtos.ChallengeSummaryPayload
import mx.cubiccoding.model.utils.getDefaultFormattedDateFromServerDate

class StudentSummaryChallengeViewHolder(view: View): StudentScoreboardViewHolder(view) {
    override fun bind(item: ScoreboardSummaryDataItem) {
        val data: ChallengeSummaryPayload = item.getData()

        //Set title
        itemView.challengeTitle.text = data.questionTitle

        //Set Description
        itemView.challengeDescription.text = data.description

        //Set Date
        itemView.challengeDate.text = getDefaultFormattedDateFromServerDate(data.createdDate)

        //Set score
        itemView.score.text = getScore(data.maxScore ?: 0, data.scoredRatio ?: 0F)
    }
}
