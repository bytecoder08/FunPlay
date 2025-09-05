package com.bytecoder.funplay.ui.player

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bytecoder.funplay.databinding.ActivityVideoPlayerBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.bytecoder.funplay.player.PlayerManager
import com.bytecoder.funplay.player.SubtitleHandler
import com.bytecoder.funplay.viewmodel.PlayerViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class VideoPlayerActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_VIDEO_URI = "extra_uri"
        const val EXTRA_VIDEO_TITLE = "extra_title"
        const val EXTRA_VIDEO_PATH = "extra_path"
    }

    private lateinit var bind: ActivityVideoPlayerBinding
    private val vm: PlayerViewModel by viewModels()
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val player = vm.player
        bind.playerView.player = player

        bind.playerView.setControllerVisibilityListener(
            StyledPlayerView.ControllerVisibilityListener { vis ->
                bind.volumeSlider.visibility = if (vis == View.VISIBLE) View.VISIBLE else View.GONE
            }
        )

        // ✅ safely parse String back into Uri
        val uriString = intent.getStringExtra(EXTRA_VIDEO_URI)
        val uri = uriString?.let { Uri.parse(it) }
        val title = intent.getStringExtra(EXTRA_VIDEO_TITLE) ?: "Video"
        val path = intent.getStringExtra(EXTRA_VIDEO_PATH) ?: ""

        if (uri != null) {
            var item = MediaItem.fromUri(uri)
            // optional: attach sidecar subtitles if present
            com.bytecoder.funplay.data.repository.SubtitleRepository.guessFromFileSidecar(path)?.let { sub ->
                item = SubtitleHandler.attachExternal(item, sub.uri, sub.mimeType, sub.label)
            }

            PlayerManager.get(this).setSingle(item)
            scope.launch { PlaybackConfig.applySaved(player, this@VideoPlayerActivity) }
        }

        bind.volumeSlider.addOnChangeListener { _, value, _ ->
            player.volume = value
            scope.launch { PlaybackConfig.saveVolume(this@VideoPlayerActivity, value) }
        }

        bind.btnOptions.setOnClickListener {
            PlayerOptionsDialog().show(supportFragmentManager, "opts")
        }
    }

    override fun onStart() { super.onStart(); vm.player.playWhenReady = true }
    override fun onStop() { super.onStop(); vm.player.playWhenReady = false }
    override fun onDestroy() { super.onDestroy(); if (isFinishing) PlayerManager.get(this).pause() }
}
