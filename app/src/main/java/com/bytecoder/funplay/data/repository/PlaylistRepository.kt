package com.bytecoder.funplay.data.repository

import android.net.Uri
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.model.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepository(private val dao: PlaylistDao) {

    fun playlists(): Flow<List<Playlist>> = dao.playlists()
    fun items(playlistId: Long): Flow<List<PlaylistItem>> = dao.items(playlistId)
    fun getVideosInPlaylist(playlistId: Long): Flow<List<Video>> =
        dao.getVideosInPlaylist(playlistId).map { entities ->
            entities.map { entity ->
                Video(
                    uri = Uri.parse(entity.path),
                    title = entity.title,
                    durationMs = entity.duration,
                    path = entity.path
                )
            }
        }


    suspend fun create(name: String): Long = dao.insertPlaylist(Playlist(name = name))
    suspend fun delete(playlist: Playlist) = dao.deletePlaylist(playlist)

    suspend fun addItem(
        playlistId: Long,
        videoId: Long,
        orderIndex: Int
    ) = dao.upsertItem(
        PlaylistItem(
            playlistId = playlistId,
            videoId = videoId,
            orderIndex = orderIndex
        )
    )

    suspend fun removeItem(playlistId: Long, videoId: Long) = dao.deleteItem(playlistId, videoId)

    suspend fun clear(playlistId: Long) = dao.clearPlaylist(playlistId)
}
