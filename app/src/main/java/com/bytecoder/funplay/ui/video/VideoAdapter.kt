package com.bytecoder.funplay.ui.video

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytecoder.funplay.data.model.Video
import com.bytecoder.funplay.databinding.ItemVideoBinding
import com.bytecoder.funplay.utils.TimeUtils

class VideoAdapter(private val onClick: (Video) -> Unit)
    : ListAdapter<Video, VideoAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Video>() {
        override fun areItemsTheSame(old: Video, new: Video) = old.path == new.path
        override fun areContentsTheSame(old: Video, new: Video) = old == new
    }

    inner class VH(val bind: ItemVideoBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val v = getItem(position)
        holder.bind.title.text = v.title
        holder.bind.duration.text = TimeUtils.format(v.durationMs)
        Glide.with(holder.itemView).load(v.uri).into(holder.bind.thumbnail)
        holder.itemView.setOnClickListener { onClick(v) }
    }
}
