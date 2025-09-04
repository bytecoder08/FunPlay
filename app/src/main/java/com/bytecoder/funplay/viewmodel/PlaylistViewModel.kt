package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.bytecoder.funplay.data.model.Playlist
import com.bytecoder.funplay.data.repository.AppDatabase
import com.bytecoder.funplay.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = PlaylistRepository(AppDatabase.get(app).playlistDao())
    val playlists: LiveData<List<Playlist>> = repo.playlists().asLiveData()

    fun create(name: String) = viewModelScope.launch { repo.create(name) }
}
