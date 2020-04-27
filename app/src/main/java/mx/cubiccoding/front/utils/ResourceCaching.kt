package mx.cubiccoding.front.utils

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import mx.cubiccoding.front.CubicCodingApplication

val colorsCache = mutableMapOf<Int, Int>()
fun getCachedColor(@ColorRes colorResId: Int): Int {
    return colorsCache.getOrPut(colorResId) {
        ContextCompat.getColor(CubicCodingApplication.instance, colorResId)
    }
}