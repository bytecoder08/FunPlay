package com.bytecoder.funplay.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.databinding.ItemPlaylistVideoBinding
import com.bytecoder.funplay.utils.TimeUtils

class PlaylistDetailAdapter : RecyclerView.Adapter<PlaylistDetailAdapter.VH>() {
    private val items = mutableListOf<PlaylistItem>()

    fun submitList(newList: List<PlaylistItem>) {
        items.clear(); items.addAll(newList); notifyDataSetChanged()
    }

    inner class VH(val bind: ItemPlaylistVideoBinding) : RecyclerView.ViewHolder(bind.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaylistVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val v = items[position]
        holder.bind.title.text = v.title
        holder.bind.duration.text = TimeUtils.format(v.durationMs)
    }
}
