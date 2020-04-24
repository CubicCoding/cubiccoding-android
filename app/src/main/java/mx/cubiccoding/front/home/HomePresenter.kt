package mx.cubiccoding.front.home

import android.app.Activity
import android.view.MenuItem
import mx.cubiccoding.R
import mx.cubiccoding.front.mvp.BaseMVPPresenter
import mx.cubiccoding.front.utils.isActivityAlive

class HomePresenter: BaseMVPPresenter<HomeViewContract, HomeModel>() {

    var lastSelectedMenuItem = R.id.item_profile

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
                viewContract.navigateToNews()
            }
        }
        lastSelectedMenuItem = selectedItem

        return true
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
}
