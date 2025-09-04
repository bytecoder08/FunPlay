package com.bytecoder.funplay.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytecoder.funplay.databinding.FragmentPlaylistBinding
import com.bytecoder.funplay.viewmodel.PlaylistViewModel

class PlaylistFragment : Fragment() {
    private var _bind: FragmentPlaylistBinding? = null
    private val bind get() = _bind!!
    private val vm: PlaylistViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentPlaylistBinding.inflate(inflater, container, false).also { _bind = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = PlaylistAdapter {
            startActivity(Intent(requireContext(), PlaylistDetailActivity::class.java).apply {
                putExtra(PlaylistDetailActivity.EXTRA_PLAYLIST_ID, it.id)
                putExtra(PlaylistDetailActivity.EXTRA_PLAYLIST_NAME, it.name)
            })
        }
        bind.recycler.layoutManager = LinearLayoutManager(requireContext())
        bind.recycler.adapter = adapter
        bind.fabAdd.setOnClickListener {
            vm.create("My Playlist " + System.currentTimeMillis().toString().takeLast(4))
        }
        vm.playlists.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    override fun onDestroyView() { _bind = null; super.onDestroyView() }
}
