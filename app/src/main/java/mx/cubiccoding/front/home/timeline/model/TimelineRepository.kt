package mx.cubiccoding.front.home.timeline.model

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.dtos.TimelineStepPayload
import mx.cubiccoding.model.networking.calls.TimelineRequest
import mx.cubiccoding.model.utils.Constants.Companion.ONE_HOUR_IN_MS
import mx.cubiccoding.persistence.database.CubicCodingDB
import mx.cubiccoding.persistence.database.timeline.TimelineEntity
import mx.cubiccoding.persistence.preferences.TimelineMetadata
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

object TimelineRepository {

    private const val EXPIRE_TIMELINE_CONTENT_AFTER_TIME_IN_MS = ONE_HOUR_IN_MS * 24

    @WorkerThread
    @Synchronized
    fun getTimelineInfo(classroomName: String, forceNetworkCall: Boolean): TimelineInfo? {
        Timber.d("Track, get timeline info")
        return try {
            //Decide whether to go to server or local...
            val timeline = CubicCodingDB.getDatabaseInstance().getTimelineDao().getByClassroomName(classroomName)
            val isTimelineTableEmpty = timeline == null

            Timber.d("Track, timeline validation forced: $forceNetworkCall, expired: ${isTimelineCacheExpired()}, table empty: $isTimelineTableEmpty")
            if (forceNetworkCall || isTimelineCacheExpired() || isTimelineTableEmpty) {
                processTimelineInfoFromNetwork(classroomName)
            } else {
                Timber.d("Track, Get timeline from database...")
                convertTimelineFromDatabase(timeline!!)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error while getting timeline in repository")
            null
        }
    }

    private fun processTimelineInfoFromNetwork(classroomName: String): TimelineInfo {
        val steps = TimelineRequest.getPrinciplesTimeline()
        val classroomProgress = TimelineRequest.getClassroomTimelineProgress(classroomName)
        val timelineProgress = classroomProgress.timelineProgress

        val timelineEntity = getTimelineEntityFromPayloads(classroomName, steps, timelineProgress)
        CubicCodingDB.getDatabaseInstance().getTimelineDao().insert(timelineEntity)
        TimelineMetadata.lastNetworkUpdate = System.currentTimeMillis()//If we successfully added a record cached, let's timestamp this cache...

        return TimelineInfo(steps, timelineProgress)
    }

    private fun isTimelineCacheExpired(): Boolean {
        return System.currentTimeMillis() > (TimelineMetadata.lastNetworkUpdate + EXPIRE_TIMELINE_CONTENT_AFTER_TIME_IN_MS)
    }

    private fun getTimelineEntityFromPayloads(classroomName: String, steps: List<TimelineStepPayload>, timelineProgress: Int): TimelineEntity {
        //Convert payload into storable timeline entity
        val stepsJsonArray = JSONArray()
        steps.forEach { timelineStepPayload ->
            val topics = JSONArray()
            timelineStepPayload.topics.forEach{ topic -> topics.put(topic) }

            val stepJson = JSONObject()
            stepJson.put("name", timelineStepPayload.name)
            stepJson.put("description", timelineStepPayload.description)
            stepJson.put("topics", topics)
            stepsJsonArray.put(stepJson)
        }

        //Handle database insertion...
        return TimelineEntity(null, classroomName, stepsJsonArray.toString(), timelineProgress)
    }

    @WorkerThread
    private fun convertTimelineFromDatabase(timelineEntity: TimelineEntity): TimelineInfo {
        val timelineProgress = timelineEntity.timelineProgress ?: 0
        val stepsJSONArray = JSONArray(timelineEntity.timelineSteps)
        val steps = mutableListOf<TimelineStepPayload>()
        for (index in 0 until stepsJSONArray.length()) {
            val stepsJson = stepsJSONArray.getJSONObject(index)
            val jsonTopics = stepsJson.getJSONArray("topics")

            //Flush the topics from the object...
            val topics = mutableListOf<String>()
            for (topicIndex in 0 until jsonTopics.length()) { topics.add(jsonTopics.getString(topicIndex)) }

            steps.add(TimelineStepPayload(stepsJson.getString("name"), stepsJson.getString("description"), topics))
        }

        return TimelineInfo(steps, timelineProgress)
    }

    data class TimelineInfo(val timeline: List<TimelineStepPayload>, val currentProgress: Int) {

    }
}