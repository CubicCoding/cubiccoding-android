package mx.cubiccoding.front.home

interface HomeViewContract {

    fun navigateToProfile()
    fun navigateToScoreboard()
    fun navigateToNews()
    fun showQuestionBottomFragment(uuid: String)

}