package mx.cubiccoding.front.home

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import com.donaumorgen.utel.model.pubsub.PubsubEvents
import mx.cubiccoding.R
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment
import mx.cubiccoding.front.mvp.BaseMVPPresenter
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.front.utils.isActivityAlive
import mx.cubiccoding.model.pubsub.Pubsub
import timber.log.Timber

class HomePresenter: BaseMVPPresenter<HomeViewContract, HomeModel>(), Pubsub.Listener {

    var lastSelectedMenuItem = R.id.item_profile

    override fun init(viewContract: HomeViewContract, model: HomeModel) {
        super.init(viewContract, model)
        Pubsub.INSTANCE.addListener(this, PubsubEvents.LAUNCH_STUDENT_SCOREBOARD_FRAGMENT)
    }

    override fun terminate() {
        super.terminate()
        Pubsub.INSTANCE.removeListener(PubsubEvents.LAUNCH_STUDENT_SCOREBOARD_FRAGMENT, this)
    }

    fun navigationSelected(item: MenuItem): Boolean {
        val selectedItem = item.itemId
        //Nothing to do when reselecting the tab that is not news and also there's no unread badge...
        if (shouldIgnoreNavigationAction(selectedItem)) return true

        when (selectedItem) {
            R.id.item_profile -> {
                viewContract.navigateToProfile()
            }
            R.id.item_scoreboard -> {
                viewContract.navigateToScoreboard()
            }
            R.id.item_help ->  {
                viewContract.navigateToHelp()
            }
            R.id.item_progress_achievement ->  {
                viewContract.navigateToTimeline()
            }
        }
        lastSelectedMenuItem = selectedItem

        return true
    }

    fun presentResume(intent: Intent?) {
        if (intent != null && IntentUtils.isNotLaunchedFromHistory(intent)) {
            when(intent.action) {
                Home.OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION -> {
                    try {
                        val uuid =
                            intent.getStringExtra(GetTestBottomDialogFragment.TEST_UUID_PRE_POPULATED_KEY)
                                ?: ""
                        if (uuid.isNotEmpty()) {
                            viewContract.showQuestionBottomFragment(uuid)
                        } else {
                            Timber.e("Track, the uuid passaed in the deeplink for opening question is empty....")
                        }
                    }finally {
                        intent.action = ""
                        intent.putExtra(GetTestBottomDialogFragment.TEST_UUID_PRE_POPULATED_KEY, "")//Always cleanup after getting the value...
                    }
                }
            }
        }
    }

    private fun shouldIgnoreNavigationAction(currentlySelectedItem: Int): Boolean {
        val viewContract = this.viewContract
        return if (viewContract is Activity && !isActivityAlive(viewContract)) {
            true
        } else if (lastSelectedMenuItem == R.id.item_help && currentlySelectedItem == R.id.item_help) {
            false
        }else  {
            val willBeIgnored = lastSelectedMenuItem == currentlySelectedItem
            //TODO: Take an action when the currentlySelectedItem element will be ignored...
            willBeIgnored
        }
    }

    override fun onEventReceived(data: Pubsub.PubsubData?) {
        when(data?.eventType) {
            PubsubEvents.LAUNCH_STUDENT_SCOREBOARD_FRAGMENT -> {
                viewContract.showStudentScoreboardFragment(data.getData())
            }
        }
    }
}
