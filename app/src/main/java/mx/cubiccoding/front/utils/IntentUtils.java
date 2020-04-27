package mx.cubiccoding.front.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import mx.cubiccoding.R;
import mx.cubiccoding.front.home.Home;
import timber.log.Timber;

import static mx.cubiccoding.front.utils.NavigatorWebLinks.PARAM_WEB_URL;

public class IntentUtils {

    public static void launchHomeActivity(Context context) {
        Intent intent = new Intent(context, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isNotLaunchedFromHistory(@Nullable Intent intent) {
        if (intent != null) {
            int flags = intent.getFlags();
            return (flags & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0;
        } else {
            return true;
        }
    }

    public static void launchWebTab(Context context, String url) {
        Timber.d("Track, Launch webview with url: %s", url);
        if (context != null && URLUtil.isValidUrl(url)) {
            try {
                String packageName = CustomTabsHelper.getPackageNameToUse(context);
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.setShowTitle(true).build();

                customTabsIntent.intent.setPackage(packageName);
                customTabsIntent.launchUrl(context, Uri.parse(url));
            } catch (Exception e) {
                Timber.e(e, "Error when trying to get packagename to launch url...");
                handleNoChrome(context, url);
            }
        }
    }

    private static void handleNoChrome(Context context, String url) {
        //If something goes wrong, then launch custom Web View...
        Toast.makeText(context, R.string.chrome_not_found, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, NavigatorWebLinks.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PARAM_WEB_URL, url);
        context.startActivity(intent);
    }

    public static void launchMarket(Context context, String appPackage) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
