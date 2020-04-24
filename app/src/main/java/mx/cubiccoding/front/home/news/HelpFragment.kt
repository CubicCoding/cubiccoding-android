package mx.cubiccoding.front.home.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.help_fragment.*
import mx.cubiccoding.R
import mx.cubiccoding.front.utils.views.loadImageCircle
import mx.cubiccoding.model.utils.fromHtml


class HelpFragment: Fragment() {

    companion object {

        const val TAG = "HelpFragment"
        fun newInstance(): HelpFragment{
            val fragment = HelpFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.help_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        context?.apply {
            //Aaron views
            loadImageCircle(this, "https://www.cubiccoding.mx/images/aaronarce.jpg", aaronAvatar)
            aaronEmail.text = fromHtml("<a href=\"mailto:aaronarce02@gmail.com\">aaronarce02@gmail.com</a>")
            aaronEmail.movementMethod = LinkMovementMethod.getInstance()
            aaronWhatsapp.setOnClickListener {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=526691017257&text=Duda CubicCoding:")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            aaronCall.setOnClickListener {
                val uri = Uri.parse("tel://6691017257")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            //Martin views
            loadImageCircle(this, "https://www.cubiccoding.mx/images/martincazares.jpg", martinAvatar)
            martinEmail.text = fromHtml("<a href=\"mailto:jm.cazaresg@gmail.com\">jm.cazaresg@gmail.com</a>")
            martinEmail.movementMethod = LinkMovementMethod.getInstance()
            martinWhatsapp.setOnClickListener {
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=526691160070&text=Duda CubicCoding:")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            martinCall.setOnClickListener {
                val uri = Uri.parse("tel://526691160070")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }


        }
    }
}
