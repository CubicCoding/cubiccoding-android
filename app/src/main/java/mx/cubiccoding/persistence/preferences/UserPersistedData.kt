package mx.cubiccoding.persistence.preferences

import android.R.attr.timeZone
import android.content.Context
import android.content.SharedPreferences
import mx.cubiccoding.front.CubicCodingApplication
import mx.cubiccoding.model.dtos.CreateDatePayload
import mx.cubiccoding.model.dtos.LoginResponsePayload
import mx.cubiccoding.model.utils.getDefaultDateFormattedNoTimeFromDate
import java.text.SimpleDateFormat
import java.util.*


object UserPersistedData {

    private var userSharedPrefs: SharedPreferences? = null
    private const val USER_PERSISTED_MODEL = "user.persisted.model"
    private const val EMAIL_KEY = "email.key"
    private const val USERNAME_KEY = "username.key"
    private const val NAME_KEY = "names.key"
    private const val FIRST_SURNAME_KEY = "first.surname.key"
    private const val SECOND_SURNAME_KEY = "second.surname.key"
    private const val AVATAR_KEY = "avatar.key"
    private const val COURSE_NAME = "course.key"
    private const val CLASSROOM_NAME = "classroom.key"
    private const val CREATED_DATE_KEY = "created.date.key"
    private const val TOKEN_KEY = "token.key"
    private const val LOGGED_KEY = "is.logged.key"
    private const val FIREBASE_TOKEN_KEY = "firebase.token.key"
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    var email: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                EMAIL_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    EMAIL_KEY,
                    value
                )
            }
        }

    var username: String
        get() {
            checkDevicePreferenceInit();
            return userSharedPrefs?.getString(USERNAME_KEY, "") ?: ""
        }
        set(value) {
            savePref {
                putString(
                    USERNAME_KEY,
                    value
                )
            }
        }

    var name: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                NAME_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    NAME_KEY,
                    value
                )
            }
        }

    var firstSurname: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                FIRST_SURNAME_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    FIRST_SURNAME_KEY,
                    value
                )
            }
        }

    var secondSurname: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                SECOND_SURNAME_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    SECOND_SURNAME_KEY,
                    value
                )
            }
        }

    var avatar: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                AVATAR_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    AVATAR_KEY,
                    value
                )
            }
        }

    var courseName: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                COURSE_NAME, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    COURSE_NAME,
                    value
                )
            }
        }

    var classroomName: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                CLASSROOM_NAME, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    CLASSROOM_NAME,
                    value
                )
            }
        }

    var createdDate: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                CREATED_DATE_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    CREATED_DATE_KEY,
                    value
                )
            }
        }

    var ccToken: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(
                TOKEN_KEY, ""
            ) ?: ""
        }
        set(value) {
            savePref {
                putString(
                    TOKEN_KEY,
                    value
                )
            }
        }

    var firebaseToken: String
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getString(FIREBASE_TOKEN_KEY, "") ?: ""
        }
        set(value) {
            savePref {
                putString(FIREBASE_TOKEN_KEY, value)
            }
        }

    var isLogged: Boolean
        get() {
            checkDevicePreferenceInit();return userSharedPrefs?.getBoolean(LOGGED_KEY, false) ?: false
        }
        set(value) {
            savePref {
                putBoolean(
                    LOGGED_KEY,
                    value
                )
            }
        }

    fun saveUserModel(userResponseBody: LoginResponsePayload?) {
        userResponseBody ?: return

        name = userResponseBody.name ?: ""
        username = userResponseBody.username ?: ""
        firstSurname = userResponseBody.firstSurname ?: ""
        secondSurname = userResponseBody.secondSurname ?: ""
        avatar = userResponseBody.avatarUrl ?: ""
        courseName = userResponseBody.courseName ?: ""
        classroomName = userResponseBody.classroomName ?: ""
        createdDate = localizeDate(userResponseBody.createDate)
        email = userResponseBody.email ?: ""//Make sure we are pointing to the right email...

    }

    private fun localizeDate(createDatePayload: CreateDatePayload?): String {
        val datePayload = createDatePayload?.date ?: return ""
        val timePayload = createDatePayload.time ?: return ""

        calendar.clear()
        calendar.apply {
            set(Calendar.YEAR, datePayload.year ?: 0)
            set(Calendar.MONTH, ((datePayload.month ?: 0) - 1).coerceAtLeast(0))
            set(Calendar.DAY_OF_MONTH, datePayload.day ?: 0)
            set(Calendar.HOUR, timePayload.hour ?: 0)
            set(Calendar.MINUTE, timePayload.minute ?: 0)
            set(Calendar.SECOND, timePayload.second ?: 0)
        }
        return getDefaultDateFormattedNoTimeFromDate(calendar.time)
    }

    fun deleteUser() {
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
                USER_PERSISTED_MODEL, Context.MODE_PRIVATE
            )
        }
    }
}
