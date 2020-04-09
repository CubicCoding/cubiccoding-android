package mx.cubiccoding.front.utils

import android.app.Activity
import androidx.fragment.app.Fragment

fun isActivityAlive(activity: Activity?): Boolean {
    return !(activity == null || activity.isFinishing || activity.isDestroyed)
}

fun isFragmentAlive(fragment: Fragment?): Boolean {
    return fragment != null && fragment.isAdded && isActivityAlive(fragment.activity)
}