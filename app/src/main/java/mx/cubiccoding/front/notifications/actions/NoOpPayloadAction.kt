package mx.cubiccoding.front.notifications.actions

import org.json.JSONObject
import timber.log.Timber

class NoOpPayloadAction(private val payload: JSONObject): FirebasePayloadAction {
    override fun execute() {
        Timber.e("Track, server sent an invalid notification with payload: $payload")
    }
}