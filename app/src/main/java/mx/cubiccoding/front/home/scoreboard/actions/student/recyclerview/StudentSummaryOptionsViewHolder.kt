package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.multioptions_summary_item.view.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.getCachedColor
import mx.cubiccoding.model.dtos.MultipleOptionsSummaryPayload
import mx.cubiccoding.model.utils.getDefaultFormattedDateFromServerDate

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

        bindOptions(questionOptions, realAnswers, userAnswered)
    }

    private fun bindOptions(questionOptions: List<String>, realAnswers: List<Int>, userAnswered: List<Int>) {
        val calculatedMarginRequiredForOptions = itemView.context.resources.getDimension(R.dimen.common_edge_space).toInt()
        val layoutInflater = LayoutInflater.from(itemView.context)
        itemView.questionOptions.removeAllViews()//Clean the layout to rebind it...
        for (index in questionOptions.indices) {
            val optionTextView = layoutInflater.inflate(R.layout.option_textview, null) as TextView
            val color = if (realAnswers.contains(index)) {
                if (userAnswered.contains(index)) { getCachedColor(R.color.option_correct) }
                else { getCachedColor(R.color.option_unanswered) }
            } else {
                if (userAnswered.contains(index)) { getCachedColor(R.color.option_incorrect) }
                else { getCachedColor(R.color.option_default) }
            }
            optionTextView.text = questionOptions[index]
            optionTextView.setTextColor(color)
            itemView.questionOptions.addView(optionTextView)
            //Programmatically after adding the view into the parent change it's margin
            (optionTextView.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = calculatedMarginRequiredForOptions
        }
    }
}
