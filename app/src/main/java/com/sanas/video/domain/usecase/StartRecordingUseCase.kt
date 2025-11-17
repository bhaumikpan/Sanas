package com.sanas.video.domain.usecase

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat

import java.util.concurrent.Executor
import javax.inject.Inject

class StartRecordingUseCase @Inject constructor() {

    operator fun invoke(
        context: Context,
        recorder: Recorder,
        executor: Executor,
        onEvent: (VideoRecordEvent) -> Unit
    ): Pair<Recording?, Uri?> {

        Log.d("StartRecordingUseCase", "🔥 StartRecordingUseCase invoked")

        val fileName = "VID_${System.currentTimeMillis()}.mp4"

        // Folder: Movies/com.sanas.video/
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/com.sanas.video/")
        }

        val mediaStoreOptions = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        // Must capture savedUri BEFORE recording (CameraX provides this)
        val savedUri = mediaStoreOptions.contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (savedUri == null) {
            Log.e("StartRecordingUseCase", "❌ Failed to create MediaStore entry")
            return Pair(null, null)
        }

        Log.d("StartRecordingUseCase", "📁 MediaStore created → $savedUri")

        // Prepare recording (audio or no audio)
        val recordingPrepared =
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

                Log.d("StartRecordingUseCase", "🎤 Audio enabled")
                recorder.prepareRecording(context, mediaStoreOptions)
                    .withAudioEnabled()

            } else {
                Log.w("StartRecordingUseCase", "⚠️ No RECORD_AUDIO permission → silent video")
                recorder.prepareRecording(context, mediaStoreOptions)
            }

        Log.d("StartRecordingUseCase", "▶️ Starting recording…")

        // Start
        val recording = try {
            recordingPrepared.start(executor) { event ->
                onEvent(event)
            }
        } catch (e: Exception) {
            Log.e("StartRecordingUseCase", "❌ Failed to start recording", e)
            return Pair(null, null)
        }

        Log.d("StartRecordingUseCase", "🎥 Recording started OK")

        return Pair(recording, savedUri)
    }
}
