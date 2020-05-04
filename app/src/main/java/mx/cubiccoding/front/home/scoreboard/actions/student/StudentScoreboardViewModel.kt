package mx.cubiccoding.front.home.scoreboard.actions.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.front.home.scoreboard.model.ScoreRepository
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.persistence.preferences.UserPersistedData

class StudentScoreboardViewModel(private val email: String, private val tournamentId: Int): ViewModel() {

    private val summary: MutableLiveData<ScoreboardRequest.ScoreboardRequestResult?> by lazy {
        MutableLiveData<ScoreboardRequest.ScoreboardRequestResult?>().also {
            getSummaryFromUser(email, tournamentId)
        }
    }

    fun getSummaryLiveData(): LiveData<ScoreboardRequest.ScoreboardRequestResult?>  = summary

    fun getSummaryFromUser(email: String, tournamentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            summary.postValue()
        }
    }
}
