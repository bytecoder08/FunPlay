package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bytecoder.funplay.player.PlayerManager

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val player = PlayerManager.get(app).exoPlayer
}
