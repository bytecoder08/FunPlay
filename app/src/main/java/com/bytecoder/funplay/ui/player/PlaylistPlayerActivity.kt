package com.bytecoder.funplay.ui.player

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.repository.AppDatabase
import com.bytecoder.funplay.databinding.ActivityPlaylistPlayerBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.bytecoder.funplay.player.PlayerManager
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModel
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.google.android.exoplayer2.MediaItem

class PlaylistPlayerActivity : AppCompatActivity() {
    companion object { const val EXTRA_PLAYLIST_ID = "playlist_id" }

    private lateinit var bind: ActivityPlaylistPlayerBinding
    private val playlistId: Long by lazy { intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L) }

    // Use a factory to supply playlistId into the ViewModel if you created such a factory.
    private val viewModel: PlaylistDetailViewModel by viewModels {
        PlaylistDetailViewModelFactory(application, playlistId)
    }

    private val player by lazy { PlayerManager.get(this).exoPlayer }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlaylistPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.playerView.player = player

        // observe playlist items (Flow -> lifecycleScope)
        lifecycleScope.launch {
            viewModel.items.asFlow().collectLatest { list ->
                if (!list.isNullOrEmpty()) {
                    // convert PlaylistItem -> MediaItem
                    val media = list.mapNotNull { item ->
                        try {
                            MediaItem.fromUri(android.net.Uri.fromFile(java.io.File(item.path)))
                        } catch (e: Exception) {
                            null
                        }
                    }
                    PlayerManager.get(this@PlaylistPlayerActivity).setPlaylist(media)
                    // restore saved playback config (speed/volume)
                    PlaybackConfig.applySaved(player, this@PlaylistPlayerActivity)
                }
            }
        }

        bind.btnOptions.setOnClickListener {
            PlayerOptionsDialog().show(supportFragmentManager, "options")
        }
    }

    override fun onStart() { super.onStart(); player.playWhenReady = true }
    override fun onStop() { super.onStop(); player.playWhenReady = false }
}
