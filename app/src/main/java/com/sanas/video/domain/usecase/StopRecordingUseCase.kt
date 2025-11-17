package com.sanas.video.domain.usecase


import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.camera.video.Recording
import javax.inject.Inject

class StopRecordingUseCase @Inject constructor() {

    operator fun invoke(
        context: Context,
        recording: Recording?,
        savedUri: Uri?
    ) {
        if (recording == null || savedUri == null) {
            Log.e("StopRecordingUseCase", "❌ Nothing to stop")
            return
        }

        Log.d("StopRecordingUseCase", "🛑 Stopping recording…")

        recording.stop()

        // Mark the file as visible
        val cv = ContentValues().apply {
            put(MediaStore.Video.Media.IS_PENDING, 0)
        }

        val rows = context.contentResolver.update(savedUri, cv, null, null)

        Log.d("StopRecordingUseCase", "📦 Finalized rows=$rows uri=$savedUri")
    }
}
