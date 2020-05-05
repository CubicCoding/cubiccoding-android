package mx.cubiccoding.front.home.scoreboard.actions.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview.ScoreboardSummaryDataItem
import mx.cubiccoding.front.home.scoreboard.model.ScoreRepository
import mx.cubiccoding.model.dtos.ScoreboardUserSummary
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.persistence.preferences.UserPersistedData

class StudentScoreboardViewModel(private val email: String, private val tournamentId: Int): ViewModel() {

    private val summary = MutableLiveData<ScoreboardSummaryModelResult?>()
    var isLoading = MutableLiveData<Boolean>()
    fun getSummaryLiveData(): LiveData<ScoreboardSummaryModelResult?>  = summary

    fun getSummaryFromUser(email: String = this.email, tournamentId: Int = this.tournamentId) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            val scoreboardSummary = ScoreboardRequest.getScoreboardUserSummary(email, tournamentId)

            //Convert to Summary items
            val multipleOptions = scoreboardSummary.multipleOptions.map { ScoreboardSummaryDataItem(ScoreboardSummaryDataItem.ScoreboardSummaryItemType.MULTI_OPTIONS, it) }
            val challenges = scoreboardSummary.challenges.map { ScoreboardSummaryDataItem(ScoreboardSummaryDataItem.ScoreboardSummaryItemType.CHALLENGES, it) }
            summary.postValue(ScoreboardSummaryModelResult(multipleOptions, challenges))
            isLoading.postValue(false)
        }
    }

    data class ScoreboardSummaryModelResult(val multipleOptions: List<ScoreboardSummaryDataItem>, val challenges: List<ScoreboardSummaryDataItem>)
}
