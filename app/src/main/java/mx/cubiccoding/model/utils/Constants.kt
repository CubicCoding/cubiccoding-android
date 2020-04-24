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
        const val CUBICCODING_MX_URL = "https://www.cubiccoding.mx/"
//        val CUBICCODING_MANAGER_URL = "https://cubiccoding-api.herokuapp.com/"
        const val CUBICCODING_MANAGER_URL = "http://192.168.0.14:8080/"
        const val HTTP_WAIT_TIME_IN_SECS = 30L
        const val HTTP_RESOURCE_NOT_FOUND = 404
        const val HTTP_RESOURCE_GONE  = 410
        const val HTTP_UNPROCESABLE_ENTITY  = 422
        const val AUTHORIZATON_HEADER = "Authorization"
        const val CC_TOKEN_PREFIX = "Bearer "
        //================== HTTP Constants ==================


        //================== Registration Constants ==================
        const val MIN_USERNAME_LENGTH = 8
        const val MIN_PASSWORD_LENGTH = 8
        //================== Registration Constants ==================


        //================== Miscellaneous ==================
        const val DATABASE_NAME = "cubiccodingdb"
        const val ONE_HOUR_IN_MS = 1000 * 60 * 60
        const val UPLOAD_ANSWER_WORK_NAME = "upload_work"
        const val TEST_UUID_WORKER_INPUT = "test.uuid.worker.input"
        const val ANSWERS_WORKER_INPUT = "answers.worker.input"
        const val FIREBASE_TOKEN_WORKER_INPUT = "firebase.token.worker.input"
        const val FIREBASE_EMAIL_WORKER_INPUT = "firebase.email.worker.input"
        const val UPLOAD_ANSWER_DELAY_IN_MS = 90000L
        const val ANDROID_DEVICE = "android"
        //================== Miscellaneous ==================
    }
}