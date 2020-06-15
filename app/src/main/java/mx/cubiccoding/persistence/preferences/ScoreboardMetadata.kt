package mx.cubiccoding.persistence.preferences

import android.content.Context
import android.content.SharedPreferences
import mx.cubiccoding.front.CubicCodingApplication

object ScoreboardMetadata {

    private var scoreboardSharedPrefs: SharedPreferences? = null
    private const val SCOREBOARD_METADATA = "scoreboard.metadata"
    private const val LAST_DOWNLOAD_TIME = "last.download.time"
    private const val LAST_ACTIVE_TOURNAMENT_NAME = "last.active.tournament.name"
    private const val LAST_ACTIVE_TOURNAMENT_ID = "last.active.tournament.id"

    var lastNetworkUpdate: Long
        get() {
            checkDevicePreferenceInit();return scoreboardSharedPrefs?.getLong(LAST_DOWNLOAD_TIME, 0L) ?: 0L }
        set(value) {
            savePref { putLong(LAST_DOWNLOAD_TIME, value) }
        }

    var lastActiveTournamentName: String
        get() {
            checkDevicePreferenceInit();return scoreboardSharedPrefs?.getString(LAST_ACTIVE_TOURNAMENT_NAME, "") ?: "" }
        set(value) {
            savePref { putString(LAST_ACTIVE_TOURNAMENT_NAME, value) }
        }

    var lastActiveTournamentId: Int
        get() {
            checkDevicePreferenceInit();return scoreboardSharedPrefs?.getInt(LAST_ACTIVE_TOURNAMENT_ID, 0) ?: 0 }
        set(value) {
            savePref { putInt(LAST_ACTIVE_TOURNAMENT_ID, value) }
        }

    fun deleteScorboardMetadata() {
        checkDevicePreferenceInit()
        scoreboardSharedPrefs?.edit()?.clear()?.apply()
    }

    private fun savePref(codeBlock: SharedPreferences.Editor.() -> Unit) {
        checkDevicePreferenceInit()
        val editor = scoreboardSharedPrefs?.edit()
        editor?.apply { codeBlock(this) }
        editor?.apply()
    }

    @Synchronized
    private fun checkDevicePreferenceInit() {
        if (scoreboardSharedPrefs == null) {
            val context = CubicCodingApplication.instance
            scoreboardSharedPrefs = context.getSharedPreferences(
                SCOREBOARD_METADATA, Context.MODE_PRIVATE)
        }
    }
}
