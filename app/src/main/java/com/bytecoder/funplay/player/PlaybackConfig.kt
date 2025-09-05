package com.bytecoder.funplay.player

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.exoplayer2.ExoPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "playback_prefs")

object PlaybackConfig {
    private val KEY_VOLUME = floatPreferencesKey("volume")
    private val KEY_SPEED  = floatPreferencesKey("speed")

    suspend fun applySaved(player: ExoPlayer, context: Context) {
        val prefs = context.dataStore.data.first()
        val vol = prefs[KEY_VOLUME] ?: 1f
        val spd = prefs[KEY_SPEED] ?: 1f
        player.volume = vol
        player.setPlaybackSpeed(spd)
    }

    suspend fun saveVolume(context: Context, volume: Float) {
        context.dataStore.edit { it[KEY_VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun saveSpeed(context: Context, speed: Float) {
        context.dataStore.edit { it[KEY_SPEED] = speed.coerceIn(0.5f, 2f) }
    }
}
