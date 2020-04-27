package mx.cubiccoding.model.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.model.utils.Constants
import timber.log.Timber

class UploadAnswerWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        Timber.e("Track, Started UploadAnswer WORK!!!")
        val testUuid = inputData.getString(Constants.TEST_UUID_WORKER_INPUT) ?: ""
        val answers = inputData.getIntArray(Constants.ANSWERS_WORKER_INPUT) ?: intArrayOf()

        return if (testUuid.isEmpty() || answers.isEmpty()) {//This is just defensive, we should never get here because if we don't have the right info we should not start the manager...
            Result.failure()
        } else {
            when (ScoreboardRequest.uploadAnswer(testUuid, answers.toList()).status) {
                ScoreboardRequest.UploadAnswerStatus.SUCCESS ->  {
                    Timber.e("Track, UploadAnswer WORK: Success")
                    Result.success()
                }
                ScoreboardRequest.UploadAnswerStatus.REQUIRES_RETRY -> {
                    Timber.e("Track, UploadAnswer WORK: Retry")
                    Result.retry()
                }
                ScoreboardRequest.UploadAnswerStatus.CANCEL_TASK -> {
                    Timber.e("Track, UploadAnswer WORK: Cancel")
                    Result.failure()
                }
            }
        }
    }
}