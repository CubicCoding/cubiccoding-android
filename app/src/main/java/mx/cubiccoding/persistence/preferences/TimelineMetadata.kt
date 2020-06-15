package mx.cubiccoding.persistence.preferences

import android.content.Context
import android.content.SharedPreferences
import mx.cubiccoding.front.CubicCodingApplication

object TimelineMetadata {

    private var timelineMetadataPrefs: SharedPreferences? = null
    private const val TIMELINE_METADATA = "timeline.metadata"
    private const val LAST_DOWNLOAD_TIME = "last.download.time"

    var lastNetworkUpdate: Long
        get() {
            checkDevicePreferenceInit();return timelineMetadataPrefs?.getLong(LAST_DOWNLOAD_TIME, 0L) ?: 0L }
        set(value) {
            savePref { putLong(LAST_DOWNLOAD_TIME, value) }
        }

    fun deleteTimelineMetadata() {
        checkDevicePreferenceInit()
        timelineMetadataPrefs?.edit()?.clear()?.apply()
    }

    private fun savePref(codeBlock: SharedPreferences.Editor.() -> Unit) {
        checkDevicePreferenceInit()
        val editor = timelineMetadataPrefs?.edit()
        editor?.apply { codeBlock(this) }
        editor?.apply()
    }

    @Synchronized
    private fun checkDevicePreferenceInit() {
        if (timelineMetadataPrefs == null) {
            val context = CubicCodingApplication.instance
            timelineMetadataPrefs = context.getSharedPreferences(
                TIMELINE_METADATA, Context.MODE_PRIVATE)
        }
    }
}
