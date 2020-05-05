package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.View
import kotlinx.android.synthetic.main.multioptions_summary_item.view.*
import mx.cubiccoding.model.dtos.MultipleOptionsSummaryPayload
import mx.cubiccoding.model.utils.fromHtml
import java.lang.StringBuilder

class StudentSummaryOptionsViewHolder(view: View): StudentScoreboardViewHolder(view) {
    override fun bind(item: ScoreboardSummaryDataItem) {
        val data: MultipleOptionsSummaryPayload = item.getData()
        itemView.questionTitle.text = data.questionTitle

        val userAnswered = data.userAnswers
        val realAnswers = data.answers
        val questionOptions = data.options

        val optionsBuilder = StringBuilder()
        //Process question options
        for (index in questionOptions.indices) {
            val color = if (realAnswers.contains(index)) {
                if (userAnswered.contains(index)) {
                    "#17A05D"//Correct
                } else {
                    "#22A5F1" // User didn't answer a correct answer
                }
            } else {
                if (userAnswered.contains(index)) {
                    "#C43023" //Incorrect
                } else {
                    "#747474" //Irrelevant...
                }
            }
            optionsBuilder.append("<b>- </b><font color=\"$color\">${questionOptions[index]}</font><br/>")
        }
        itemView.questionOptions.text = fromHtml(optionsBuilder.toString())

        //Set score
        val scoreReceived = (data.maxScore ?: 0) * (data.scoredRatio ?: 0F)
        itemView.score.text = "${scoreReceived.toInt()}/${data.maxScore}"
    }
}
