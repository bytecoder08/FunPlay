package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.repository.AppDatabase
import com.bytecoder.funplay.data.repository.PlaylistRepository

class PlaylistDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = PlaylistRepository(AppDatabase.get(app).playlistDao())
    private val playlistIdLive = MutableLiveData<Long>()
    val items: LiveData<List<PlaylistItem>> = playlistIdLive.switchMap {
        id -> repo.items(id).asLiveData()
    }
    fun setPlaylistId(id: Long) { playlistIdLive.value = id }
}
