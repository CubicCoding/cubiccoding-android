package mx.cubiccoding.front.splash

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import mx.cubiccoding.front.utils.IntentUtils
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_FACEBOOK_ANDROID_URI
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_FACEBOOK_WEB_URI
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_INSTAGRAM_ANDROID_URI
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_INSTAGRAM_WEB_URI
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_TWITTER_ANDROID_URI
import mx.cubiccoding.model.utils.Constants.Companion.CUBICCODING_TWITTER_WEB_URI
import mx.cubiccoding.model.utils.Constants.Companion.FACEBOOK_ANDROID_PACKAGE_NAME
import mx.cubiccoding.model.utils.Constants.Companion.INSTAGRAM_ANDROID_PACKAGE_NAME
import mx.cubiccoding.model.utils.Constants.Companion.TWITTER_ANDROID_PACKAGE_NAME
import timber.log.Timber


class SplashNetworkActions {

    fun openOpenNetworkApp(context: Context, networkApp: NetworkApp){

        val (packageName, resourceUri, fallbackUri) = when (networkApp) {
            NetworkApp.FACEBOOK -> LaunchingAppInfo(FACEBOOK_ANDROID_PACKAGE_NAME, CUBICCODING_FACEBOOK_ANDROID_URI, CUBICCODING_FACEBOOK_WEB_URI)
            NetworkApp.TWITTER -> LaunchingAppInfo(TWITTER_ANDROID_PACKAGE_NAME, CUBICCODING_TWITTER_ANDROID_URI, CUBICCODING_TWITTER_WEB_URI)
            NetworkApp.INSTAGRAM -> LaunchingAppInfo(INSTAGRAM_ANDROID_PACKAGE_NAME, CUBICCODING_INSTAGRAM_ANDROID_URI, CUBICCODING_INSTAGRAM_WEB_URI)
        }

        val intent = try {
            context.packageManager.getPackageInfo(packageName, 0)
            Log.e("Track", "Taking the resource URI: $resourceUri")
            Intent(Intent.ACTION_VIEW, Uri.parse(resourceUri))
        } catch (e: Exception) {
            Log.e("Track", "Taking the fallback", e)
            null
        }

        //Go internal or launch customized tab...
        if (intent != null) {
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.e("Error trying to launch")
                showFancyToast(context, "Error al lanzar app...", Gravity.BOTTOM)
            }
        }
        else { IntentUtils.launchWebTab(context, fallbackUri) }
    }

    enum class NetworkApp { FACEBOOK, TWITTER, INSTAGRAM }
    data class LaunchingAppInfo(val packageName: String, val resourceUri: String, val fallbackUri: String)

}