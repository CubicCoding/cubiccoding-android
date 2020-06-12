package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.View
import kotlinx.android.synthetic.main.multioptions_summary_item.view.*
import kotlinx.android.synthetic.main.question_option_item.view.*
import mx.cubiccoding.model.dtos.MultipleOptionsSummaryPayload
import mx.cubiccoding.model.utils.fromHtml
import mx.cubiccoding.model.utils.getDefaultFormattedDateFromServerDate
import java.lang.StringBuilder

class StudentSummaryOptionsViewHolder(view: View): StudentScoreboardViewHolder(view) {
    override fun bind(item: ScoreboardSummaryDataItem) {
        val data: MultipleOptionsSummaryPayload = item.getData()
        //Set title
        itemView.questionTitle.text = data.questionTitle

        processOptionsBinding(data)

        //Set data
        itemView.questionDate.text = getDefaultFormattedDateFromServerDate(data.createdDate)

        //Set score
        itemView.score.text = getScore(data.maxScore ?: 0, data.scoredRatio ?: 0F)
    }

    private fun processOptionsBinding(data: MultipleOptionsSummaryPayload) {
        val userAnswered = data.userAnswers
        val realAnswers = data.answers
        val questionOptions = data.options

        val optionsBuilder = StringBuilder()
        //Process question options//TODO: Add a better way to display the topics...
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
    }
}
