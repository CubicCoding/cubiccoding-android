package mx.cubiccoding.front.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import mx.cubiccoding.R
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.front.home.Home
import mx.cubiccoding.front.home.Home.Companion.OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION
import mx.cubiccoding.front.home.scoreboard.actions.GetTestBottomDialogFragment.Companion.TEST_UUID_PRE_POPULATED_KEY
import mx.cubiccoding.front.home.scoreboard.questions.TestActivity
import mx.cubiccoding.front.home.scoreboard.questions.TestActivity.Companion.TEST_ID_EXTRA
import mx.cubiccoding.front.splash.Splash
import mx.cubiccoding.model.utils.Constants
import mx.cubiccoding.persistence.preferences.UserPersistedData

class NotificationPayloadAction(
    private val title: String,
    private val message: String,
    private val action: String,
    private val pushData: String
) : FirebasePayloadAction {

    companion object {
        private const val BASIC_NOTIFICATION_CHANNEL_ALL = "basic.notification.channel"
    }

    private var channel: NotificationChannel? = null

    override fun execute() {
        if (!UserPersistedData.isLogged) return//Nothing to do if the user is not logged in...

        val context = CubicCodingApplication.instance
        val intent = when(action) {
                Constants.NEW_SCORE_TEST_ACTION_VALUE -> {
                    Intent(context, Home::class.java).apply {
                        action = OPEN_BOTTOM_QUESTION_FRAGMENT_ACTION
                        putExtra(TEST_UUID_PRE_POPULATED_KEY, pushData)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                }
                else -> {
                    Intent(context, Splash::class.java)
                }
            }
        val snoozePendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, BASIC_NOTIFICATION_CHANNEL_ALL)
            .setSmallIcon(R.drawable.ic_question_answer)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setContentIntent(snoozePendingIntent)

        fireNotification(context, pushData.hashCode(), builder)
    }

    private fun fireNotification(context: Context, notificationId: Int, builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (channel == null) {
                    //This channel properties will be used across all the notifications that use this channel...
                    channel = NotificationChannel(
                        BASIC_NOTIFICATION_CHANNEL_ALL,
                        BASIC_NOTIFICATION_CHANNEL_ALL, NotificationManager.IMPORTANCE_DEFAULT
                    ).apply {//Add extra channel configuration...
                        enableVibration(false)
                    }
                }
                channel?.apply { createNotificationChannel(this) }
            }// android version < "O"

            notify(notificationId, builder.build())
        }
    }
}
