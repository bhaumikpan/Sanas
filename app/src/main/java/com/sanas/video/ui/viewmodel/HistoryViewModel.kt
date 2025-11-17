package com.sanas.video.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanas.video.data.repo.CameraRepository
import com.sanas.video.domain.model.RecordedVideo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: CameraRepository
) : ViewModel() {

    private val _videos = MutableLiveData<List<RecordedVideo>>()
    val videos: LiveData<List<RecordedVideo>> = _videos

    fun load() {
        viewModelScope.launch {
            val list = repo.getAllVideos()
            _videos.postValue(list)
        }
    }
}
