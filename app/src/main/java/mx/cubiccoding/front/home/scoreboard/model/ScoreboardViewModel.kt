package mx.cubiccoding.front.home.scoreboard.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScoreboardViewModel: ViewModel() {

    private val scores: MutableLiveData<ScoreRepository.ScoreRepositoryResult?> by lazy { MutableLiveData<ScoreRepository.ScoreRepositoryResult?>().also { loadScores() } }
    private val repository: ScoreRepository by lazy { ScoreRepository() }

    fun getScoresLiveData(): LiveData<ScoreRepository.ScoreRepositoryResult?>  = scores

    fun loadScores(forceNetworkCall: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            scores.postValue(repository.getScores(forceNetworkCall))
        }
    }
}
