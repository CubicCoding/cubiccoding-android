package mx.cubiccoding.front.home.scoreboard.actions.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.home.scoreboard.actions.student.recyclerview.ScoreboardSummaryDataItem
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import timber.log.Timber
import java.lang.Exception

class StudentScoreboardViewModel(private val email: String, private val tournamentId: Int): ViewModel() {

    private val summary = MutableLiveData<ScoreboardSummaryModelResult?>()
    var isLoading = MutableLiveData<Boolean>()
    var showError = MutableLiveData<Boolean>()
    fun getSummaryLiveData(): LiveData<ScoreboardSummaryModelResult?>  = summary

    fun getSummaryFromUser(email: String = this.email, tournamentId: Int = this.tournamentId) {
        viewModelScope.launch(Dispatchers.IO) {

            //Reset state slates...
            isLoading.postValue(true)
            showError.postValue(false)

            val scoreboardSummary = try {
                ScoreboardRequest.getScoreboardUserSummary(email, tournamentId)
            } catch (e: Exception) {
                Timber.e(e, "Error getting summary for user")
                showError.postValue(true)
                null
            }

            //Convert to Summary items
            val multipleOptions = scoreboardSummary?.multipleOptions?.map { ScoreboardSummaryDataItem(ScoreboardSummaryDataItem.ScoreboardSummaryItemType.MULTI_OPTIONS, it) }
            val challenges = scoreboardSummary?.challenges?.map { ScoreboardSummaryDataItem(ScoreboardSummaryDataItem.ScoreboardSummaryItemType.CHALLENGES, it) }
            summary.postValue(ScoreboardSummaryModelResult(multipleOptions ?: emptyList(), challenges ?: emptyList()))
            isLoading.postValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        summary.postValue(null)
    }

    data class ScoreboardSummaryModelResult(val multipleOptions: List<ScoreboardSummaryDataItem>, val challenges: List<ScoreboardSummaryDataItem>)
}
