package mx.cubiccoding.front.home.scoreboard.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mx.cubiccoding.R

class StudentScoreboardFragment: Fragment() {

    companion object {
        const val TAG = "StudentScoreboardFragment"
        const val EMAIL_EXTRA_KEY = "email.extra.key"

        fun newInstance(email: String?): StudentScoreboardFragment {
            val fragment = StudentScoreboardFragment()
            val args = Bundle()
            email?.apply { args.putString(EMAIL_EXTRA_KEY, this) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment_2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}
