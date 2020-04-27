package mx.cubiccoding.model.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import mx.cubiccoding.R
import mx.cubiccoding.model.networking.CubicCodingRequestException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
val defaultDateFormatter = SimpleDateFormat("dd'/'MM'/'yyyy', 'hh:mm aaa", Locale.getDefault())
@Synchronized
fun getDefaultFormattedDateFromMillis(milliseconds: Long): String {
    return defaultDateFormatter.format(Date(milliseconds)).capitalize()
}

@Suppress("DEPRECATION")
fun fromHtml(html: String): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(html)
    }
}

fun getErrorMessageForVoucherVerification(context: Context, error: Throwable): String {
    return if (error is CubicCodingRequestException) {
        when (error.httpStatusCode) {
            Constants.HTTP_RESOURCE_NOT_FOUND -> context.getString(R.string.voucher_doesnt_exist)
            Constants.HTTP_RESOURCE_GONE -> context.getString(R.string.voucher_already_used)
            else -> context.getString(R.string.invalid_voucher)
        }
    } else context.getString(R.string.error_processing_voucher)
}

fun getErrorMessageForRegistration(context: Context, error: Throwable): String {
    return if (error is CubicCodingRequestException) {
        when (error.httpStatusCode) {
            Constants.HTTP_UNPROCESABLE_ENTITY -> context.getString(R.string.user_already_exists)
            Constants.HTTP_RESOURCE_GONE -> context.getString(R.string.user_already_exists)
            else -> context.getString(R.string.error_during_register)
        }
    } else context.getString(R.string.error_during_register)
}