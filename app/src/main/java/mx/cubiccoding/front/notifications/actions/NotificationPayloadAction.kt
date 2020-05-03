package mx.cubiccoding.front.notifications.actions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import mx.cubiccoding.R
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.front.home.Home
import mx.cubiccoding.front.home.Home.Companion.OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment.Companion.TEST_UUID_PRE_POPULATED_KEY
import mx.cubiccoding.model.utils.Constants
import mx.cubiccoding.persistence.preferences.UserPersistedData
import java.util.concurrent.ThreadLocalRandom


class NotificationPayloadAction(
    private val title: String,
    private val message: String,
    private val action: String,
    private val pushData: String
) : FirebasePayloadAction {

    companion object {
        private const val BASIC_NOTIFICATION_CHANNEL_ALL = "basic.notification.channel"
        private const val CHALLENGES_NOTIFICATION_CHANNEL_ALL = "challenges.notification.channel"
    }

    private var actionChannel: NotificationChannel? = null

    override fun execute() {
        if (!UserPersistedData.isLogged) return//Nothing to do if the user is not logged in...

        val context = CubicCodingApplication.instance

        actionChannel = getChannelByAction(context)
        val notificationId = getNotificationId()
        val notification = getNotificationFromData(context, notificationId)
        fireNotification(context, notificationId, notification)
    }

    private fun getNotificationFromData(context: Context, notificationId: Int): Notification? {
        return when(action) {
            Constants.NEW_SCORE_OPTIONS_TEST_ACTION_VALUE -> createScoreOptionsNotification(context, notificationId)
            Constants.NEW_SCORE_CHALLENGE_TEST_ACTION_VALUE -> createScoreChallengesNotification(context, notificationId)
            else -> null
        }
    }

    private fun createScoreOptionsNotification(context: Context, notificationId: Int): Notification? {
        val notificationIcon = R.drawable.ic_cc_no_bg
        val intent = Intent(context, Home::class.java).apply {
            action = OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION
            putExtra(TEST_UUID_PRE_POPULATED_KEY, pushData)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val openOptionQuestions = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(context, BASIC_NOTIFICATION_CHANNEL_ALL)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setContentIntent(openOptionQuestions).build()
    }

    private fun createScoreChallengesNotification(context: Context, notificationId: Int): Notification? {
        val notificationIcon = R.drawable.ic_cc_no_bg
        val intent = Intent(context, Home::class.java)
        val openApplication = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.applicationContext.packageName + "/" + R.raw.beep)
        return NotificationCompat.Builder(context, CHALLENGES_NOTIFICATION_CHANNEL_ALL)
            .setSmallIcon(notificationIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setSound(soundUri)
            .setContentIntent(openApplication).build()
    }

    private fun getNotificationId(): Int =
        // This logic could be updated based on our needs, for now the default is the hash of the push data or random if no data is provided
        if (pushData.isNotEmpty()) {
            pushData.hashCode()
        } else {
            ThreadLocalRandom.current().nextInt(1, 1000)
        }

    private fun fireNotification(context: Context, notificationId: Int, notification: Notification?) {
        notification ?: return//Nothing to do without a notification...

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (actionChannel == null) {//This is the default channel safety net in case there's no specific channel for the action...
                    actionChannel = NotificationChannel(
                        BASIC_NOTIFICATION_CHANNEL_ALL,
                        BASIC_NOTIFICATION_CHANNEL_ALL, NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {//Add extra channel configuration...
                        enableVibration(false)
                    }
                }
                actionChannel?.apply { createNotificationChannel(this) }
            }// android version < "O"
            notify(notificationId, notification)
        }
    }

    private fun getChannelByAction(context: Context): NotificationChannel? {
        //This channel properties will be used across all the notifications that use this channel...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return when(action) {
                Constants.NEW_SCORE_CHALLENGE_TEST_ACTION_VALUE -> {
                    NotificationChannel(
                        CHALLENGES_NOTIFICATION_CHANNEL_ALL,
                        CHALLENGES_NOTIFICATION_CHANNEL_ALL, NotificationManager.IMPORTANCE_HIGH
                    ).apply {//Add extra channel configuration...
                        enableVibration(false)
                        val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.applicationContext.packageName + "/" + R.raw.beep)
                        val audioAttributes = AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
                        setSound(soundUri, audioAttributes)
                    }
                }
                else -> null
            }
        }

        return null
    }
}
