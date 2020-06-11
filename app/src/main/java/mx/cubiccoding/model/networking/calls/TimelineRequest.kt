package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.dtos.TimelineProgressPayload
import mx.cubiccoding.model.dtos.TimelineStepPayload
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.networking.RequestsManager

object TimelineRequest {

    @WorkerThread
    fun getPrinciplesTimeline(): List<TimelineStepPayload> {
        val response = RequestsManager.cubicCodingManagerApi.getPrinciplesTimeline().execute()
        return when {
            response.isSuccessful -> {
                response.body() ?: throw CubicCodingRequestException(
                        "Get principles timeline response body is null...",
                        RequestErrorType.NULL_BODY,
                        response.code()
                    )
            }
            else -> {
                throw CubicCodingRequestException(
                    "Get principles timeline request not successful",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
            }
        }
    }

    @WorkerThread
    fun getClassroomTimelineProgress(classroomName: String): TimelineProgressPayload {
        val response = RequestsManager.cubicCodingManagerApi.getClassroomTimelineProgress(classroomName).execute()
        return when {
            response.isSuccessful -> {
                response.body() ?: throw CubicCodingRequestException(
                    "Get classroom's timeline progress response body is null...",
                    RequestErrorType.NULL_BODY,
                    response.code()
                )
            }
            else -> {
                throw CubicCodingRequestException(
                    "Get classroom's timeline progress request not successful",
                    RequestErrorType.UNSUCCESS,
                    response.code()
                )
            }
        }
    }
}
