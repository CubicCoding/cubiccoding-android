package mx.cubiccoding.front.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.home_activity.*
import mx.cubiccoding.R
import mx.cubiccoding.front.home.news.HelpFragment
import mx.cubiccoding.front.home.profile.ProfileFragment
import mx.cubiccoding.front.home.scoreboard.ScoreboardFragment
import timber.log.Timber


class Home : AppCompatActivity(), HomeViewContract {

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

    private fun setupViews() {
        bottomNavigation.post {
            navigateToProfile()
            bottomNavigation.selectedItemId = R.id.item_profile
            bottomNavigation.setOnNavigationItemSelectedListener(presenter::navigationSelected)
        }
    }

    override fun onBackPressed() {
        if (presenter.lastSelectedMenuItem == R.id.item_profile) {
            super.onBackPressed()
        } else {
            bottomNavigation.selectedItemId = R.id.item_profile
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
        navigateToFragment(ProfileFragment.newInstance(), ProfileFragment.TAG)
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

}
