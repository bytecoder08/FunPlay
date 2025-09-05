package com.bytecoder.funplay.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

class PlayerManager private constructor(private val context: Context) {

    val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build()
    }

    private fun dataSourceFactory() = DefaultDataSource.Factory(context)

    fun mediaSourceFor(mediaItem: MediaItem): MediaSource =
        ProgressiveMediaSource.Factory(dataSourceFactory()).createMediaSource(mediaItem)

    fun setSingle(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setPlaylist(items: List<MediaItem>) {
        val sources = items.map { mediaSourceFor(it) }
        val cat = ConcatenatingMediaSource().apply { sources.forEach { addMediaSource(it) } }
        exoPlayer.setMediaSource(cat)
        exoPlayer.prepare()
    }

    fun play() { exoPlayer.playWhenReady = true }
    fun pause() { exoPlayer.playWhenReady = false }
    fun release() { exoPlayer.release() }

    companion object {
        @Volatile private var INSTANCE: PlayerManager? = null
        fun get(context: Context): PlayerManager =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlayerManager(context.applicationContext).also { INSTANCE = it }
            }
    }
}
