package com.sanas.video.ui.viewmodel


import android.app.Application
import android.net.Uri
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sanas.video.domain.usecase.StartRecordingUseCase
import com.sanas.video.domain.usecase.StopRecordingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    application: Application,
    private val startUseCase: StartRecordingUseCase,
    private val stopUseCase: StopRecordingUseCase
) : AndroidViewModel(application) {

    private var currentRecording: Recording? = null
    private var savedUri: Uri? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording

    private val _lastEvent = MutableStateFlow<VideoRecordEvent?>(null)
    val lastEvent: StateFlow<VideoRecordEvent?> = _lastEvent

    fun toggleRecording(recorder: Recorder) {
        if (_isRecording.value) stop() else start(recorder)
    }

    private fun start(recorder: Recorder) {
        val context = getApplication<Application>()
        val executor = ContextCompat.getMainExecutor(context)

        viewModelScope.launch {
            val (recording, uri) = startUseCase(
                context = context,
                recorder = recorder,
                executor = executor
            ) { event ->
                _lastEvent.value = event
            }

            currentRecording = recording
            savedUri = uri
            _isRecording.value = true
        }
    }


    private fun stop() {
        val context = getApplication<Application>()

        stopUseCase(
            context = context,
            recording = currentRecording,
            savedUri = savedUri
        )

        currentRecording = null
        savedUri = null
        _isRecording.value = false
    }
}
