package com.sanas.video.ui.history

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.sanas.video.databinding.FragmentHistoryBinding
import com.sanas.video.domain.model.RecordedVideo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private var adapter = VideoAdapter { uri ->
        // start player activity or VideoView here
        playVideo(uri)
    }
    private val TAG = "HistoryFragment"

    private fun playVideo(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Open system chooser so user selects app
        val chooser = Intent.createChooser(intent, "Open video with…")
        startActivity(chooser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        context?.let { val videos = loadVideosFromMediaStore(it)
            adapter.submitList(videos)
            adapter.notifyDataSetChanged()
        }
    }

    fun loadVideosFromMediaStore(context: Context): List<RecordedVideo> {
        Log.d("HistoryLoader", "📂 Loading videos from Movies/com.sanas.video/")

        val videos = mutableListOf<RecordedVideo>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ?"
        val args = arrayOf("%Movies/com.sanas.video/%")

        val sort = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            args,
            sort
        )

        if (cursor == null) {
            Log.e("HistoryLoader", "❌ Cursor is null")
            return emptyList()
        }

        Log.d("HistoryLoader", "Cursor count = ${cursor.count}")

        cursor.use {
            val idCol = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durCol = it.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)

            while (it.moveToNext()) {
                val id = it.getLong(idCol)
                val name = it.getString(nameCol)
                val duration = it.getLong(durCol)

                val uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                Log.d("HistoryLoader", "🎬 Found $name → $uri")

                videos.add(RecordedVideo(uri,duration,System.currentTimeMillis()))
            }
        }

        return videos
    }
}
