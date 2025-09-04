package com.bytecoder.funplay.player

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

object AudioTrackHandler {
    fun selectByLanguage(player: ExoPlayer, language: String?) {
        val selector = player.trackSelector as? DefaultTrackSelector ?: return
        val params = selector.buildUponParameters().setPreferredAudioLanguage(language).build()
        selector.setParameters(params)
    }
}
