package mx.cubiccoding.model.firebase_messaging

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mx.cubiccoding.front.notifications.FirebaseNotificationHandler
import mx.cubiccoding.persistence.preferences.UserPersistedData
import timber.log.Timber

class CubicCodingFirebaseMessaging: FirebaseMessagingService() {

    val firebaseNotificationHandler by lazy { FirebaseNotificationHandler() }

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
        firebaseNotificationHandler.handlePushPayload(data.data)
    }
}
