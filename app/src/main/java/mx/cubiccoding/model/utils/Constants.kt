package mx.cubiccoding.model.utils

class Constants {
    companion object {
        //================== Network Actions Constants ==================
        const val FACEBOOK_ANDROID_PACKAGE_NAME = "com.facebook.katana"
        const val CUBICCODING_FACEBOOK_ANDROID_URI = "fb://facewebmodal/f?href=https://www.facebook.com/cubiccoding/"
        const val CUBICCODING_FACEBOOK_WEB_URI = "https://www.facebook.com/cubiccoding"

        const val TWITTER_ANDROID_PACKAGE_NAME = "com.twitter.android"
        const val CUBICCODING_TWITTER_ANDROID_URI = "twitter://user?screen_name=cubiccoding"
        const val CUBICCODING_TWITTER_WEB_URI = "https://twitter.com/cubiccoding"

        const val INSTAGRAM_ANDROID_PACKAGE_NAME = "com.instagram.android"
        const val CUBICCODING_INSTAGRAM_ANDROID_URI = "http://instagram.com/_u/cubiccoding"
        const val CUBICCODING_INSTAGRAM_WEB_URI = "http://instagram.com/cubiccoding"
        //================== Network Actions Constants ==================

        //================== HTTP Constants ==================
        val CUBICCODING_MX_URL = "https://www.cubiccoding.mx/"
//        val CUBICCODING_MANAGER_URL = "https://cubiccoding-api.herokuapp.com/"
        val CUBICCODING_MANAGER_URL = "http://192.168.0.14:8080/"
        val HTTP_WAIT_TIME_IN_SECS = 30L
        val HTTP_RESOURCE_NOT_FOUND = 404
        val HTTP_RESOURCE_GONE  = 410
        //================== HTTP Constants ==================


        //================== HTTP Constants ==================
        val DELAY_NAVIGATING_TO_ACTIVITY_FROM_BOTTOM_ACTION = 300L
    }
}