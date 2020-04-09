package mx.cubiccoding.front.utils.views

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import mx.cubiccoding.R

fun showFancyToast(context: Context?, message: String, gravity: Int = (Gravity.CENTER)) {
    val textView = LayoutInflater.from(context).inflate(R.layout.fancy_toast, null) as TextView
    textView.text = message
    Toast(context).apply {
        view = textView
        setGravity(gravity, 0, 0)
    }.show()
}