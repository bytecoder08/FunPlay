package com.bytecoder.funplay.ui.player

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bytecoder.funplay.databinding.ActivityPlaylistPlayerBinding
import com.bytecoder.funplay.player.PlayerManager
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModel
import com.bytecoder.funplay.viewmodel.PlaylistDetailViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistPlayerActivity : AppCompatActivity() {
    private lateinit var bind: ActivityPlaylistPlayerBinding
    private val viewModel: PlaylistDetailViewModel by viewModels {
        PlaylistDetailViewModelFactory(application, intent.getLongExtra("playlistId", -1))
    }
    private val player by lazy { PlayerManager.get(this).exoPlayer }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlaylistPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Attach ExoPlayer to UI
        bind.playerView.player = player

        // Observe playlist videos
        lifecycleScope.launch {
            viewModel.videos.collectLatest { list ->
                if (list.isNotEmpty()) {
                    PlayerManager.get(this@PlaylistPlayerActivity).playList(list)
                }
            }
        }

        // Options dialog
        bind.btnOptions.setOnClickListener {
            PlayerOptionsDialog().show(supportFragmentManager, "options")
        }
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        player.playWhenReady = false
    }
}
