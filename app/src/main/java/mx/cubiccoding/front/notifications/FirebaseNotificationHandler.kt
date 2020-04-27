package mx.cubiccoding.front.notifications

import mx.cubiccoding.model.utils.Constants
import mx.cubiccoding.model.utils.Constants.Companion.COMMAND_TYPE_VALUE
import mx.cubiccoding.model.utils.Constants.Companion.PAYLOAD_ACTION_PROPERTY
import mx.cubiccoding.model.utils.Constants.Companion.PAYLOAD_DATA_PROPERTY
import mx.cubiccoding.model.utils.Constants.Companion.PAYLOAD_MESSAGE_PROPERTY
import mx.cubiccoding.model.utils.Constants.Companion.PAYLOAD_TITLE_PROPERTY
import mx.cubiccoding.model.utils.Constants.Companion.NOTIFICATION_TYPE_VALUE
import mx.cubiccoding.model.utils.Constants.Companion.PAYLOAD_TYPE_PROPERTY
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.lang.IllegalArgumentException

class FirebaseNotificationHandler {

    fun handlePushPayload(payload: Map<String, String>) {
        val payloadContent = payload[Constants.PAYLOAD_CONTENT_VALUE]
        try {
            val content = JSONObject(payloadContent)
            val payloadAction = when (content[PAYLOAD_TYPE_PROPERTY]) {
                NOTIFICATION_TYPE_VALUE -> NotificationPayloadAction(
                    content.optString(PAYLOAD_TITLE_PROPERTY),
                    content.optString(PAYLOAD_MESSAGE_PROPERTY),
                    content.optString(PAYLOAD_ACTION_PROPERTY),
                    content.optString(PAYLOAD_DATA_PROPERTY)
                )
                COMMAND_TYPE_VALUE -> CommandPayloadAction(
                    content.optString(PAYLOAD_ACTION_PROPERTY),
                    content.optString(PAYLOAD_DATA_PROPERTY))

                else -> throw IllegalArgumentException("Invalid payload action...")
            }

            //Take action based on payload...
            payloadAction.execute()

        } catch (e: Exception) {
            Timber.e(e, "ERROR in Firebase notification handler")
            null
        }
    }
}
