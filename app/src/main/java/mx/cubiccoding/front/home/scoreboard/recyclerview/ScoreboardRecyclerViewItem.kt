package mx.cubiccoding.front.home.scoreboard.recyclerview

class ScoreboardRecyclerViewItem(val type: ScoreboardItemType, private val info: Any) {

    enum class ScoreboardItemType {
        FIRST_PLACE,
        NON_FIRST_PLACE,
        HEADER
    }

    //util method for auto casting info, this will let us know immediately if we are handling bad info...
    fun <T> getData(): T = info as T

}
