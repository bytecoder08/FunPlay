package com.bytecoder.funplay.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_items",
    primaryKeys = ["playlistId", "videoId"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(entity = VideoEntity::class, parentColumns = ["id"], childColumns = ["videoId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("playlistId"), Index("videoId")]
)
data class PlaylistItem(
    val playlistId: Long,
    val videoId: Long,
    val title: String,
    val durationMs: Long,
    val orderIndex: Int
)