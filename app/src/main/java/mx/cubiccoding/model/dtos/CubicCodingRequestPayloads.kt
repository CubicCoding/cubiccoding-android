package mx.cubiccoding.model.dtos

import mx.cubiccoding.persistence.database.scoreboard.ScoreboardEntity


//RESPONSE PAYLOADS
data class BasicResponsePayload(val success: Int?)
data class GetVoucherResponsePayload(val email: String?)
data class LoginResponsePayload(val username: String?, val name: String?, val firstSurname: String?, val secondSurname: String?,
                                val email: String?, val gender: String?, val avatarUrl: String?, val courseName: String?, val classroomName: String?, val createDate: CreateDatePayload?)
data class CreateDatePayload(val date: DatePayload?, val time: TimePayload?)
data class DatePayload(val year: Int?, val month: Int?, val day: Int?)
data class TimePayload(val hour: Int?, val minute: Int?, val second: Int?)
data class TournamentInfo(val id: Int?, val tournamentName: String?, val prize: String?)
data class ScoreboardItemPayload(val rank: Int?, val currentScore: Float?, val totalOfferedScore: Int?, val displayName: String?, val avatarUrl: String?, val email: String?) {
    fun toDBEntity() = ScoreboardEntity(null, displayName, avatarUrl, currentScore, totalOfferedScore, rank, email)
}
data class ScoreboardResponsePayload(val tournamentInfo: TournamentInfo?, val secondaries: List<ScoreboardItemPayload>)
data class GetTestResponsePayload(val uuid: String?, val label: String?, val questionTitle: String?, val scoreTestType: String, val options: List<String>, val answers: List<Int>, val maxScore: Int)

//REQUEST PAYLOADS
data class SignupRequestPayload(val email: String, val username: String, val password: String)
data class LoginRequestPayload(val username: String, val password: String)
data class UploadAnswerRequestPayload(val scoreTestUuid: String, val userAnswers: List<Int>)
data class RegisterFirebaseTokenRequestPayload(val token: String, val email: String, val device: String)
