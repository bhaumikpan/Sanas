package com.sanas.video.data.repo

import com.sanas.video.data.media.MediaStoreVideoDataSource
import com.sanas.video.domain.model.RecordedVideo
import javax.inject.Inject

class CameraRepository @Inject constructor(
    private val dataSource: MediaStoreVideoDataSource
) {
    fun getAllVideos(): List<RecordedVideo> = dataSource.fetchVideos()
}
