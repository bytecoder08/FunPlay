package com.bytecoder.funplay.ui.explorer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytecoder.funplay.databinding.ItemFileBinding

class ExplorerAdapter : RecyclerView.Adapter<ExplorerAdapter.VH>() {
    private val items = mutableListOf<Pair<Boolean,String>>() // isDir, name
    fun submitList(newList: List<Pair<Boolean,String>>) { items.clear(); items.addAll(newList); notifyDataSetChanged() }
    inner class VH(val bind: ItemFileBinding) : RecyclerView.ViewHolder(bind.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: VH, position: Int) {
        val (isDir, name) = items[position]
        holder.bind.icon.setImageResource(if (isDir) android.R.drawable.ic_menu_sort_by_size else android.R.drawable.ic_media_play)
        holder.bind.name.text = name
    }
}
