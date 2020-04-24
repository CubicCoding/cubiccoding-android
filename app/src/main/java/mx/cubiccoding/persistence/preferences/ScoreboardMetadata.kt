package mx.cubiccoding.persistence.preferences

import android.content.Context
import android.content.SharedPreferences
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.model.dtos.LoginResponsePayload

object ScoreboardMetadata {

    private var userSharedPrefs: SharedPreferences? = null
    private const val USER_PERSISTED_MODEL = "scoreboard.metadata"
    private const val LAST_DOWNLOAD_TIME = "last.download.time"
    private const val LAST_ACTIVE_TOURNAMENT = "last.active.tournament"

    var lastNetworkUpdate: Long
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getLong(LAST_DOWNLOAD_TIME, 0L) ?: 0L }
        set(value) {
            savePref { putLong(LAST_DOWNLOAD_TIME, value) }
        }

    var lastActiveTournament: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(LAST_ACTIVE_TOURNAMENT, "") ?: "" }
        set(value) {
            savePref { putString(LAST_ACTIVE_TOURNAMENT, value) }
        }

    fun deleteScorboardMetadata() {
        checkDevicePreferenceInit()
        userSharedPrefs?.edit()?.clear()?.apply()
    }

    private fun savePref(codeBlock: SharedPreferences.Editor.() -> Unit) {
        checkDevicePreferenceInit()
        val editor = userSharedPrefs?.edit()
        editor?.apply { codeBlock(this) }
        editor?.apply()
    }

    @Synchronized
    private fun checkDevicePreferenceInit() {
        if (userSharedPrefs == null) {
            val context = CubicCodingApplication.instance
            userSharedPrefs = context.getSharedPreferences(
                USER_PERSISTED_MODEL, Context.MODE_PRIVATE)
        }
    }
}
