package mx.cubiccoding.model.firebase_messaging

import android.os.Handler
import android.os.Looper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.front.utils.views.showFancyToast
import mx.cubiccoding.persistence.preferences.UserPersistedData
import timber.log.Timber

class CubicCodingFirebaseMessaging: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Timber.d("Track, New token: $token on thread: ${Thread.currentThread().name}")

        UserPersistedData.firebaseToken = token
        if (UserPersistedData.isLogged) {//If user is logged and there's a new token upload it to server otherwise wait for login...
            val email = UserPersistedData.email
            FirebaseTokenUploader.registerToken(token, email)
        }
    }

    override fun onMessageReceived(data: RemoteMessage) {
        super.onMessageReceived(data)

        Timber.e("Track, Message Data: ${data.data}")
        Handler(Looper.getMainLooper()).post {
            showFancyToast(CubicCodingApplication.instance, data.data.toString())
        }
    }
}