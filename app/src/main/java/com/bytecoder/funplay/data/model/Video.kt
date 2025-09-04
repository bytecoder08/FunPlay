package com.bytecoder.funplay.data.model

import android.net.Uri

data class Video(
    val uri: Uri,
    val title: String,
    val durationMs: Long,
    val path: String
)
