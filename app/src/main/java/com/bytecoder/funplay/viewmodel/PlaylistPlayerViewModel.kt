package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bytecoder.funplay.player.PlayerManager

class PlaylistPlayerViewModel(app: Application) : AndroidViewModel(app) {
    val player = PlayerManager.get(app).exoPlayer
}
