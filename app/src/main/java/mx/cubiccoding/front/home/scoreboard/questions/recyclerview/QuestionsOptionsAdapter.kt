package mx.cubiccoding.front.home.scoreboard.questions.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardRecyclerViewItem.ScoreboardItemType
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.recyclerview.ScoreboardViewHolder
import java.lang.IllegalArgumentException

class QuestionOptionsAdapter: RecyclerView.Adapter<OptionViewHolder>() {

    private val options = mutableListOf<OptionItem>()
    private var answers: List<Int>? = null
    private var shouldDisable = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.question_option_item, parent, false)
        return OptionViewHolder(view)

    }
    override fun getItemCount() = options.size
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) { holder.bind(options[position], shouldDisable) }

    fun populateOptions(scoreboard: List<OptionItem>, answers: List<Int>) {
        this.answers = answers
        this.options.clear()
        this.options.addAll(scoreboard)
        notifyDataSetChanged()
    }

    fun getOptionsSelected(): List<Int> {
        val optionsSelected = mutableListOf<Int>()
        for (index in 0 until options.size) {
            val optionItem = options[index]
            if (optionItem.checked) {
                optionsSelected.add(index)
            }
        }
        return optionsSelected
    }

    /**
     * This method returns feedback for inserted input
     * and returns the % deserved for the answer...
     * @return percent
     */
    fun provideFeedback(): Float {
        shouldDisable = true//Send signal to stop taking actions on the view holders...
        var optionsAnsweredCorrectly = 0F
        var answeredIncorrectly = false

        for (index in 0 until options.size) {
            val option = options[index]

            //It doesn't matter if is checked or not, let's show which is the right now...

            if (option.checked) {//Verify if is correct/incorrect...
                if (answers?.contains(index) == true) {
                    optionsAnsweredCorrectly++
                    option.resultMarker = true
                    notifyItemChanged(index)
                } else {
                    answeredIncorrectly = true
                    option.resultMarker = false
                    notifyItemChanged(index)
                }
            } else {
                notifyItemChanged(index)//notify is time to disable...
            }
        }

        return if (answeredIncorrectly) 0F
        else optionsAnsweredCorrectly / (answers?.size?.toFloat() ?: 1F)
    }

    data class OptionItem(val optionAnswer: String, var checked: Boolean = false, var resultMarker: Boolean? = null)
}
