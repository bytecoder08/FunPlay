package com.bytecoder.funplay.ui.playlist

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytecoder.funplay.databinding.ActivityPlaylistDetailBinding
import com.bytecoder.funplay.ui.player.PlaylistPlayerActivity
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModel
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModelFactory

class PlaylistDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PLAYLIST_ID = "playlist_id"
        const val EXTRA_PLAYLIST_NAME = "playlist_name"
    }

    private lateinit var bind: ActivityPlaylistDetailBinding
    private lateinit var adapter: PlaylistDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlaylistDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L)
        title = intent.getStringExtra(EXTRA_PLAYLIST_NAME) ?: "Playlist"

        // Initialize ViewModel with factory that passes playlistId
        val vm: PlaylistDetailViewModel by viewModels {
            PlaylistDetailViewModelFactory(application, playlistId)
        }

        adapter = PlaylistDetailAdapter()
        bind.recycler.layoutManager = LinearLayoutManager(this)
        bind.recycler.adapter = adapter

        // Play all button
        bind.btnPlayAll.setOnClickListener {
            startActivity(Intent(this, PlaylistPlayerActivity::class.java).apply {
                putExtra(PlaylistPlayerActivity.EXTRA_PLAYLIST_ID, playlistId)
            })
        }

        // Observe playlist items LiveData
        vm.items.observe(this) { list ->
            adapter.submitList(list)
        }
    }
}
