package mx.cubiccoding.model.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import mx.cubiccoding.model.dtos.RegisterFirebaseTokenRequestPayload
import mx.cubiccoding.model.firebase_messaging.FirebaseTokenUploader
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.model.networking.calls.UserRequest
import mx.cubiccoding.model.utils.Constants
import timber.log.Timber
import java.lang.Exception

class RegisterFirebaseTokenWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    override fun doWork(): Result {
        Timber.e("Track, Started FirebaseRegister WORK!!!")
        val firebaseToken = inputData.getString(Constants.FIREBASE_TOKEN_WORKER_INPUT) ?: ""
        val email = inputData.getString(Constants.FIREBASE_EMAIL_WORKER_INPUT) ?: ""

        return if (firebaseToken.isEmpty() || email.isEmpty()) {//This is just defensive, we should never get here because if we don't have the right info we should not start the manager...
            Result.failure()
        } else {

            val response = UserRequest.registerFirebaseToken(firebaseToken, email)
            if (response?.isSuccessful == true) {
                Timber.e("Track, FirebaseRegister WORK: Success")
                Result.success()
            } else {
                Timber.e("Track, FirebaseRegister WORK: Retry")
                Result.retry()
            }
        }
    }
}