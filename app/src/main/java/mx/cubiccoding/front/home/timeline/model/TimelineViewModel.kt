package mx.cubiccoding.front.home.timeline.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.cubiccoding.model.dtos.TimelineStepPayload
import mx.cubiccoding.persistence.preferences.UserPersistedData

class TimelineViewModel: ViewModel() {

    private val timeline: MutableLiveData<TimelineRepository.TimelineInfo> by lazy {
        MutableLiveData<TimelineRepository.TimelineInfo>().also {
            loadTimeline(UserPersistedData.classroomName)
        }
    }

    fun getTimeline(): LiveData<TimelineRepository.TimelineInfo>  = timeline

    fun loadTimeline(classroomName: String, forceNetworkCall: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            timeline.postValue(TimelineRepository.getTimelineInfo(classroomName, forceNetworkCall))
        }
    }
}
