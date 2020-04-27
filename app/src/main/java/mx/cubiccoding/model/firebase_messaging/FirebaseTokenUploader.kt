package mx.cubiccoding.model.firebase_messaging

import androidx.annotation.WorkerThread
import androidx.work.*
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.model.networking.calls.UserRequest
import mx.cubiccoding.model.utils.Constants
import mx.cubiccoding.model.workers.RegisterFirebaseTokenWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

object FirebaseTokenUploader {

    @WorkerThread
    fun registerToken(token: String, email: String) {
        val response = UserRequest.registerFirebaseToken(token, email)
        if (response?.isSuccessful == true) {
            Timber.d("Track, Firebase token registered correctly in sync'ed call")
        } else {
            Timber.d("Track, Scheduled worker thread to upload firebase token")
            val inputData = workDataOf(
                Constants.FIREBASE_TOKEN_WORKER_INPUT to token,
                Constants.FIREBASE_EMAIL_WORKER_INPUT to email
            )
            val worker = with(OneTimeWorkRequestBuilder<RegisterFirebaseTokenWorker>()) {
                setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                setInputData(inputData)
                setInitialDelay(Constants.UPLOAD_ANSWER_DELAY_IN_MS, TimeUnit.MILLISECONDS)
            }.build()
            val workManager = WorkManager.getInstance(CubicCodingApplication.instance)
            workManager.beginUniqueWork(Constants.UPLOAD_ANSWER_WORK_NAME, ExistingWorkPolicy.REPLACE, worker).enqueue()
        }
    }

}