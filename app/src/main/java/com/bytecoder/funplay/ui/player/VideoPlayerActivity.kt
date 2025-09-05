package com.bytecoder.funplay.ui.player

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bytecoder.funplay.R
import com.bytecoder.funplay.databinding.ActivityVideoPlayerBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.bytecoder.funplay.player.PlayerManager
import com.bytecoder.funplay.player.SubtitleHandler
import com.bytecoder.funplay.viewmodel.PlayerViewModel
import com.bytecoder.funplay.utils.TimeUtils
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
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

    private var isFullscreen = false
    private var fullscreenBtn: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val player = vm.player
        bind.playerView.player = player

        // === Fullscreen toggle ===
        fullscreenBtn = bind.btnFullscreen
        fullscreenBtn?.setOnClickListener { toggleFullscreen() }

        // === Load video ===
        val uriString = intent.getStringExtra(EXTRA_VIDEO_URI)
        val uri = uriString?.let { Uri.parse(it) }
        val path = intent.getStringExtra(EXTRA_VIDEO_PATH) ?: ""

        if (uri != null) {
            var item = MediaItem.fromUri(uri)
            com.bytecoder.funplay.data.repository.SubtitleRepository
                .guessFromFileSidecar(path)?.let { sub ->
                    item = SubtitleHandler.attachExternal(item, sub.uri, sub.mimeType, sub.label)
                }

            PlayerManager.get(this).setSingle(item)
            scope.launch { PlaybackConfig.applySaved(player, this@VideoPlayerActivity) }
        }

        // === Seekbar updates ===
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                bind.bufferSpinner.visibility =
                    if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                bind.btnPlayPause.setImageResource(
                    if (isPlaying) android.R.drawable.ic_media_pause
                    else android.R.drawable.ic_media_play
                )
            }
        })

        bind.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) player.seekTo(progress.toLong())
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // === Playback controls ===
        bind.btnPlayPause.setOnClickListener {
            if (player.isPlaying) player.pause() else player.play()
        }
        bind.btnForward.setOnClickListener { player.seekForward() }
        bind.btnRewind.setOnClickListener { player.seekBack() }
        bind.btnNext.setOnClickListener { player.seekToNext() }
        bind.btnPrev.setOnClickListener { player.seekToPrevious() }

        // === Sliders ===
        bind.volumeSlider.addOnChangeListener { _, value, _ ->
            player.volume = value
            scope.launch { PlaybackConfig.saveVolume(this@VideoPlayerActivity, value) }
        }
        bind.brightnessSlider.addOnChangeListener { _, value, _ ->
            setScreenBrightness(value)
        }

        // === 3-dots options ===
        bind.btnOptions.setOnClickListener {
            PlayerOptionsDialog().show(supportFragmentManager, "opts")
        }

        // === Gestures (seek, volume, brightness) ===
        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null || e2 == null) return false
                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y

                if (kotlin.math.abs(deltaX) > kotlin.math.abs(deltaY)) {
                    // Horizontal → seek
                    if (deltaX > 0) player.seekForward() else player.seekBack()
                } else {
                    // Vertical → left = brightness, right = volume
                    if (e1.x < bind.playerView.width / 2) {
                        adjustBrightness(-deltaY)
                    } else {
                        adjustVolume(-deltaY, player)
                    }
                }
                return true
            }
        })
        bind.playerView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event); true
        }

        // === Time updater ===
        Thread {
            while (!isFinishing) {
                runOnUiThread {
                    if (player.isPlaying || player.playbackState == Player.STATE_READY) {
                        bind.seekBar.max = player.duration.toInt()
                        bind.seekBar.progress = player.currentPosition.toInt()
                        bind.tvCurrent.text = TimeUtils.format(player.currentPosition)
                        bind.tvTotal.text = TimeUtils.format(player.duration)
                    }
                }
                Thread.sleep(500)
            }
        }.start()
    }

    private fun toggleFullscreen() {
        if (isFullscreen) {
            supportActionBar?.show()
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            fullscreenBtn?.setImageResource(android.R.drawable.ic_menu_crop)
        } else {
            supportActionBar?.hide()
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            fullscreenBtn?.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        }
        isFullscreen = !isFullscreen
    }

    private fun setScreenBrightness(value: Float) {
        val lp = window.attributes
        lp.screenBrightness = value
        window.attributes = lp
    }

    private fun adjustBrightness(delta: Float) {
        val lp = window.attributes
        lp.screenBrightness = (lp.screenBrightness + delta / 1000).coerceIn(0f, 1f)
        window.attributes = lp
    }

    private fun adjustVolume(delta: Float, player: com.google.android.exoplayer2.ExoPlayer) {
        player.volume = (player.volume + delta / 1000).coerceIn(0f, 1f)
        bind.volumeSlider.value = player.volume
    }

    override fun onStart() { super.onStart(); vm.player.playWhenReady = true }
    override fun onStop() { super.onStop(); vm.player.playWhenReady = false }
    override fun onDestroy() { super.onDestroy(); if (isFinishing) PlayerManager.get(this).pause() }
}
