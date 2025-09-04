package com.bytecoder.funplay.data.repository

import androidx.room.*
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.model.Video
import com.bytecoder.funplay.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("""
        SELECT v.* FROM videos v
        INNER JOIN playlist_items pi ON v.id = pi.videoId
        WHERE pi.playlistId = :playlistId
        ORDER BY pi.orderIndex ASC
    """)
    fun getVideosInPlaylist(playlistId: Long): Flow<List<VideoEntity>>

    @Query("SELECT * FROM playlists ORDER BY id DESC")
    fun playlists(): Flow<List<Playlist>>

    @Insert
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Delete
    suspend fun deletePlaylist(playlist: Playlist)

    @Query("SELECT * FROM playlist_items WHERE playlistId = :playlistId ORDER BY orderIndex ASC")
    fun items(playlistId: Long): Flow<List<PlaylistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertItem(item: PlaylistItem)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun deleteItem(playlistId: Long, videoId: Long)

    @Query("DELETE FROM playlist_items WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)
}
