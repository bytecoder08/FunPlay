package com.bytecoder.funplay.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.databinding.ItemPlaylistBinding

class PlaylistAdapter(private val onClick: (Playlist) -> Unit)
    : ListAdapter<Playlist, PlaylistAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(o: Playlist, n: Playlist) = o.id == n.id
        override fun areContentsTheSame(o: Playlist, n: Playlist) = o == n
    }
    inner class VH(val bind: ItemPlaylistBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        holder.bind.name.text = p.name
        holder.itemView.setOnClickListener { onClick(p) }
    }
}
