package com.sanas.video.data.media

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.sanas.video.domain.model.RecordedVideo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaStoreVideoDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun fetchVideos(): List<RecordedVideo> {
        val videos = mutableListOf<RecordedVideo>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED
        )

        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->

            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val duration = cursor.getLong(durationCol)
                val date = cursor.getLong(dateCol) * 1000 // convert seconds → millis

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                videos.add(
                    RecordedVideo(
                        uri = contentUri,
                        duration = duration,
                        date = date
                    )
                )
            }
        }

        return videos
    }
}
