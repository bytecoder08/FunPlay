package com.bytecoder.funplay.data.model

import android.net.Uri

data class Subtitle(
    val uri: Uri,
    val mimeType: String = "text/vtt",
    val language: String? = null,
    val label: String? = null
)
