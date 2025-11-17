package com.sanas.video.ui.history

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sanas.video.databinding.ItemVideoBinding
import com.sanas.video.domain.model.RecordedVideo

class VideoAdapter(
    private val onPlay: (Uri) -> Unit
) : RecyclerView.Adapter<VideoAdapter.VideoVH>() {

    private var items: List<RecordedVideo> = emptyList()

    fun submitList(list: List<RecordedVideo>) {
        items = list
        notifyDataSetChanged()
    }

    inner class VideoVH(val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        val binding = ItemVideoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VideoVH(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        val item = items[position]

        // Load thumbnail
        Glide.with(holder.itemView)
            .load(item.uri)
            .centerCrop()
            .into(holder.binding.imgPreview)

        // Duration
        holder.binding.txtDuration.text = "${item.duration / 1000}s"

        // PLAY BUTTON
        holder.binding.btnPlay.setOnClickListener {
            onPlay(item.uri)
        }
    }
}
