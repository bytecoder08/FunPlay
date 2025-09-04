package com.bytecoder.funplay.data.model

import androidx.room.*

@Entity(tableName = "playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
