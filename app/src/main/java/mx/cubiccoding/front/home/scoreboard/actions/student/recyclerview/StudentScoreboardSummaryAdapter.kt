package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mx.cubiccoding.R
import java.lang.IllegalStateException

class StudentScoreboardSummaryAdapter: RecyclerView.Adapter<StudentScoreboardViewHolder>() {

    private val items = mutableListOf<ScoreboardSummaryDataItem>()

    override fun getItemViewType(position: Int): Int {
        return when(items[position].type) {
            ScoreboardSummaryDataItem.ScoreboardSummaryItemType.CHALLENGES -> R.layout.challenges_summary_item
            else -> R.layout.multioptions_summary_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentScoreboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when(viewType) {
            R.layout.challenges_summary_item -> StudentSummaryChallengeViewHolder(view)
            R.layout.multioptions_summary_item -> StudentSummaryOptionsViewHolder(view)
            else -> throw IllegalStateException("No view holder type found for: $viewType")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StudentScoreboardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setData(data: List<ScoreboardSummaryDataItem>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

}
