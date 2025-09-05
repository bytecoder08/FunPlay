package com.bytecoder.funplay.ui.player

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.repository.AppDatabase
import com.bytecoder.funplay.databinding.ActivityPlaylistPlayerBinding
import com.bytecoder.funplay.player.PlaybackConfig
import com.bytecoder.funplay.player.PlayerManager
import com.bytecoder.funplay.viewmodel.PlaylistPlayerViewModel
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PlaylistPlayerActivity : AppCompatActivity() {
    companion object { const val EXTRA_PLAYLIST_ID = "playlist_id" }
    private lateinit var bind: ActivityPlaylistPlayerBinding
    private val vm: PlaylistPlayerViewModel by viewModels()
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityPlaylistPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val player = vm.player
        bind.playerView.player = player

        val playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L)
        val dao = AppDatabase.get(this).playlistDao()
        scope.launch {
            val items = dao.items(playlistId).asLiveData().value ?: emptyList()
            loadPlaylist(items)
            PlaybackConfig.applySaved(player, this@PlaylistPlayerActivity)
        }

        bind.btnOptions.setOnClickListener {
            PlayerOptionsDialog().show(supportFragmentManager, "opts")
        }
    }

    private fun loadPlaylist(items: List<PlaylistItem>) {
        val media = items.map { MediaItem.fromUri(android.net.Uri.fromFile(java.io.File(it.path))) }
        PlayerManager.get(this).setPlaylist(media)
    }

    override fun onStart() { super.onStart(); vm.player.playWhenReady = true }
    override fun onStop() { super.onStop(); vm.player.playWhenReady = false }
}
