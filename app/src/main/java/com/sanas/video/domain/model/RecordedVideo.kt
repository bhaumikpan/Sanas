package com.sanas.video.domain.model

import android.net.Uri

data class RecordedVideo(
    val uri: Uri,
    val duration: Long,
    val date: Long
)
