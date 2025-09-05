package com.bytecoder.funplay.data.model

import androidx.room.*

@Entity(
    tableName = "playlist_items",
    primaryKeys = ["playlistId", "path"]
)
data class PlaylistItem(
    val playlistId: Long,
    val path: String,
    val title: String,
    val durationMs: Long,
    val orderIndex: Int
)
