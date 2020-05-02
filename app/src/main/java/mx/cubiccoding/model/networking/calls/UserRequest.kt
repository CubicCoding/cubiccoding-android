package mx.cubiccoding.model.networking.calls

import androidx.annotation.WorkerThread
import mx.cubiccoding.model.dtos.LoginRequestPayload
import mx.cubiccoding.model.dtos.LoginResponsePayload
import mx.cubiccoding.model.dtos.RegisterFirebaseTokenRequestPayload
import mx.cubiccoding.model.dtos.SignupRequestPayload
import mx.cubiccoding.model.firebase_messaging.FirebaseTokenUploader
import mx.cubiccoding.model.networking.CubicCodingRequestException
import mx.cubiccoding.model.networking.GenericRequestListener
import mx.cubiccoding.model.networking.RequestErrorType
import mx.cubiccoding.model.networking.RequestsManager
import mx.cubiccoding.model.utils.Constants.Companion.ANDROID_DEVICE
import mx.cubiccoding.model.utils.Constants.Companion.AUTHORIZATON_HEADER
import mx.cubiccoding.persistence.preferences.UserPersistedData
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber

object UserRequest {

    @WorkerThread
    fun signup(email: String, username: String, password: String): ResponseBody {
        Timber.d("Registering user email: $email, username: $username")
        val response = RequestsManager.cubicCodingManagerApi.signup(SignupRequestPayload(email, username, password)).execute()
        when {
            response.isSuccessful -> {
                val responseBody = response.body()
                if (responseBody != null) {
                    return responseBody
                } else {
                    throw CubicCodingRequestException(
                        "RegisterPayload body is null",
                        RequestErrorType.NULL_BODY,
                        response.code()
                    )
                }
            }
            else -> throw CubicCodingRequestException(
                "RegisterPayload request failed",
                RequestErrorType.UNSUCCESS,
                response.code()
            )
        }
    }

    @WorkerThread
    fun login(username: String, password: String): LoginResponsePayload? {
        Timber.d("Logging in user: $username")
        val response = RequestsManager.cubicCodingManagerApi.login(LoginRequestPayload(username, password)).execute()
        when {
            response.isSuccessful -> {
                if (response.body() != null) {
                    val body = response.body()
                    UserPersistedData.saveUserModel(body)
                    UserPersistedData.isLogged = true

                    //Get cubiccoding's api token
                    response.headers()[AUTHORIZATON_HEADER]?.apply {
                        UserPersistedData.ccToken = this
                        Timber.d("Saved user token: $this")
                    }

                    //Now that we are logged make sure that if there is a token, this token is up on the server
                    val firebaseToken = UserPersistedData.firebaseToken
                    val userEmail = body?.email ?: ""
                    if (firebaseToken.isNotEmpty() && userEmail.isNotEmpty()) {
                        FirebaseTokenUploader.registerToken(firebaseToken, userEmail)
                    } else {
                        Timber.e("Empty value for Firebase registration firebasetoken: $firebaseToken userEmail: $userEmail")
                    }

                    return body
                } else {
                    throw CubicCodingRequestException(
                        "Logging In body is null",
                        RequestErrorType.NULL_BODY,
                        response.code()
                    )
                }
            }
            else -> throw CubicCodingRequestException(
                "Logging In request failed",
                RequestErrorType.UNSUCCESS,
                response.code()
            )
        }
    }

    @WorkerThread
    fun registerFirebaseToken(token: String, email: String): Response<ResponseBody>? {
        Timber.d("Track, Trying to register a firebasetoken with email: $email")
        return try {
            RequestsManager.cubicCodingManagerApi.registerFirebaseToken(
                RegisterFirebaseTokenRequestPayload(token, email, ANDROID_DEVICE))
                .execute()
        } catch (e: Exception) {
            Timber.e(e, "Error trying to register firebase token...")
            null
        }
    }
}
