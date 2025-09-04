package com.bytecoder.funplay.data.repository

import android.net.Uri
import com.bytecoder.funplay.data.model.Subtitle

object SubtitleRepository {
    fun guessFromFileSidecar(videoPath: String): Subtitle? {
        val base = videoPath.substringBeforeLast(".")
        val candidates = listOf("$base.srt" to "application/x-subrip", "$base.vtt" to "text/vtt")
        val file = candidates.firstOrNull { java.io.File(it.first).exists() } ?: return null
        val uri = Uri.fromFile(java.io.File(file.first))
        return Subtitle(uri = uri, mimeType = file.second, label = "External")
    }
}
