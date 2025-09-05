package com.bytecoder.funplay.data.model

import android.net.Uri

data class Video(
    val uri: Uri,
    val name: String,
    val duration: Long,
    val path: String
)
