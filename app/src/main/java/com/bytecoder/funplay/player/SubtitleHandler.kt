package com.bytecoder.funplay.player

import com.google.android.exoplayer2.MediaItem

object SubtitleHandler {
    fun attachExternal(mediaItem: MediaItem, uri: android.net.Uri, mimeType: String, label: String? = null): MediaItem {
        val subCfg = MediaItem.SubtitleConfiguration.Builder(uri)
            .setMimeType(mimeType)
            .setLanguage("en")
            .setLabel(label ?: "Subtitles")
            .setSelectionFlags(0)
            .build()
        return mediaItem.buildUpon().setSubtitleConfigurations(listOf(subCfg)).build()
    }
}
