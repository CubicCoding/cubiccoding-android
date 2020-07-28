package mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview

class ScoreboardSummaryDataItem(val type: ScoreboardSummaryItemType, private val info: Any) {

    enum class ScoreboardSummaryItemType {
        MULTI_OPTIONS,
        CHALLENGES,
        BONUS_POINTS
    }

    //util method for auto casting info, this will let us know immediately if we are handling bad info...
    fun <T> getData(): T = info as T

}
