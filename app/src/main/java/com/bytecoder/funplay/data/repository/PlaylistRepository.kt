package com.bytecoder.funplay.data.repository

import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.model.PlaylistItem
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val dao: PlaylistDao) {

    fun playlists(): Flow<List<Playlist>> = dao.playlists()
    fun items(playlistId: Long): Flow<List<PlaylistItem>> = dao.items(playlistId)

    suspend fun create(name: String): Long = dao.insertPlaylist(Playlist(name = name))
    suspend fun delete(playlist: Playlist) = dao.deletePlaylist(playlist)

    suspend fun addItem(
        playlistId: Long,
        path: String,
        title: String,
        durationMs: Long,
        orderIndex: Int
    ) = dao.upsertItem(PlaylistItem(playlistId, path, title, durationMs, orderIndex))

    suspend fun removeItem(playlistId: Long, path: String) = dao.deleteItem(playlistId, path)
    suspend fun clear(playlistId: Long) = dao.clearPlaylist(playlistId)
}
