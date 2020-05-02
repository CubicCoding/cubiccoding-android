package mx.cubiccoding.front.home.scoreboard.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.model.networking.calls.ScoreboardRequest
import mx.cubiccoding.persistence.preferences.UserPersistedData

class ScoreboardViewModel: ViewModel() {

    private val scores: MutableLiveData<ScoreboardRequest.ScoreboardRequestResult?> by lazy {
        MutableLiveData<ScoreboardRequest.ScoreboardRequestResult?>().also {
            loadScores(UserPersistedData.email, UserPersistedData.classroomName)
        }
    }

    fun getScoresLiveData(): LiveData<ScoreboardRequest.ScoreboardRequestResult?>  = scores

    fun loadScores(email: String, classroomName: String, forceNetworkCall: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            scores.postValue(ScoreRepository.getScores(email, classroomName, forceNetworkCall))
        }
    }
}
