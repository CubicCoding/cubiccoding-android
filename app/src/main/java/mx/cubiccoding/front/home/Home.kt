package mx.cubiccoding.front.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import com.donaumorgen.utel.model.pubsub.PubsubEvents
import kotlinx.android.synthetic.main.home_activity.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.news.HelpFragment
import mx.cubiccoding.front.home.profile.MyProfileFragment
import mx.cubiccoding.front.home.scoreboard.ScoreboardFragment
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment.Companion.TEST_UUID_PRE_POPULATED_KEY
import mx.cubiccoding.front.home.scoreboard.actions.StudentScoreboardFragment
import mx.cubiccoding.front.utils.isActivityAlive
import mx.cubiccoding.model.dtos.ScoreboardItemPayload
import mx.cubiccoding.model.pubsub.Pubsub
import timber.log.Timber


class Home : AppCompatActivity(), HomeViewContract {

    companion object {
        const val OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION = "open.bottom.question.fragment.action"
    }

    private val presenter by lazy { HomePresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.home_activity)
        presenter.init(this, HomeModel())
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.terminate()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        presenter.presentResume(intent)
    }

    private fun setupViews() {
        bottomNavigation.post {
            navigateToProfile()
            bottomNavigation.selectedItemId = R.id.item_profile
            bottomNavigation.setOnNavigationItemSelectedListener(presenter::navigationSelected)
        }
    }

    override fun onBackPressed() {
        if (!bottomNavigation.isEnabled) {//Leaving the student scoreboard state...
            setEnabledBottomNavigation(true)
            super.onBackPressed()//Allow fragment to leave the stack...
        } else if (presenter.lastSelectedMenuItem == R.id.item_profile) {
            super.onBackPressed()
        } else {
            bottomNavigation.selectedItemId = R.id.item_profile
        }
    }

    override fun showStudentScoreboardFragment(data: ScoreboardItemPayload) {
        runOnUiThread {
            if (isActivityAlive(this@Home)) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.flip_left_in, R.anim.flip_left_out, R.anim.flip_left_in, R.anim.flip_left_out)
                    .replace(R.id.fragmentContainer, StudentScoreboardFragment.newInstance(data.email), StudentScoreboardFragment.TAG)
                    .addToBackStack(StudentScoreboardFragment.TAG)
                    .commit()

                //Always disable the navigation bottom when showing scorboard of stundent...
               setEnabledBottomNavigation(false)
            }
        }
    }

    /**
     * All the navigate methods, go to the right fragment
     * however, is important to notice that this will not
     * update the icon in the bottom navigation bar, in
     * order to provide full navigation experience use the
     * bottomNavigation@selectedItemId method...
     */
    override fun navigateToProfile() {
        navigateToFragment(MyProfileFragment.newInstance(), MyProfileFragment.TAG)
    }

    override fun navigateToScoreboard() {
        navigateToFragment(ScoreboardFragment.newInstance(), ScoreboardFragment.TAG)
    }

    override fun navigateToNews() {
        navigateToFragment(HelpFragment.newInstance(), HelpFragment.TAG)
    }

    private fun navigateToFragment(fragment: Fragment, tag: String) {
        //Do the fragment change...
        try {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment, tag)
                .commit()
        } catch (e: Exception) {
            Timber.e("Error when navigating to fragment")
        }
    }

    override fun showQuestionBottomFragment(uuid: String) {
        val arg = with(Bundle()) {
            putString(TEST_UUID_PRE_POPULATED_KEY, uuid)
            this
        }
        GetTestBottomDialogFragment.newInstance(arg).show(supportFragmentManager, GetTestBottomDialogFragment.TAG)
    }

    private fun setEnabledBottomNavigation(enabled: Boolean) {
        bottomNavigation.alpha = if (enabled) 1F else 0.5F
        bottomNavigation.isEnabled = enabled
        bottomNavigation.menu.forEach { it.isEnabled = enabled }
    }

}
