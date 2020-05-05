package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class StudentScoreboardViewHolder(view: View): RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ScoreboardSummaryDataItem)
}