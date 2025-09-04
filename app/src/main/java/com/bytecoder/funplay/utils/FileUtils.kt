package com.bytecoder.funplay.utils

object FileUtils {
    fun isVideoName(name: String): Boolean {
        val lower = name.lowercase()
        return listOf(".mp4",".mkv",".webm",".avi",".mov",".m4v").any { lower.endsWith(it) }
    }
}
