package mx.cubiccoding.front.home

import mx.cubiccoding.model.dtos.ScoreboardItemPayload

interface HomeViewContract {

    fun navigateToProfile()
    fun navigateToScoreboard()
    fun navigateToNews()
    fun showQuestionBottomFragment(uuid: String)
    fun showStudentScoreboardFragment(data: ScoreboardItemPayload)

}