package mx.cubiccoding.front.home.scoreboard.questions.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.question_option_item.view.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.getCachedColor
import mx.cubiccoding.front.utils.views.showFancyToast

class OptionViewHolder(view: View): RecyclerView.ViewHolder(view) {

    fun bind(item: QuestionOptionsAdapter.OptionItem, shouldDisable: Boolean = false) {
        itemView.setOnClickListener {
            item.checked = !item.checked
            itemView.checkedOption.visibility = if (item.checked) View.VISIBLE else View.GONE
        }

        itemView.optionAnswer.text = item.optionAnswer
        itemView.optionNumber.text = adapterPosition.toString()
        itemView.checkedOption.visibility = if (item.checked) View.VISIBLE else View.GONE

        if (item.resultMarker != null) {
            //Show correct/incorrect
            itemView.resultMarker.visibility = View.VISIBLE
            if (item.resultMarker == true) { itemView.resultMarker.setBackgroundColor(getCachedColor(R.color.green_right_answer)) }
            else { itemView.resultMarker.setBackgroundColor(getCachedColor(R.color.error_red)) }
        } else {
            itemView.resultMarker.visibility = View.GONE
        }

        if (shouldDisable) {
            itemView.isEnabled = false//Do not allow more clicks...
        }

    }
}
