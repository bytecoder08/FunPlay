package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytecoder.funplay.data.model.PlaylistItem
import com.bytecoder.funplay.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    app: Application,
    playlistId: Long
) : AndroidViewModel(app) {
    private val repo = PlaylistRepository((app as com.bytecoder.funplay.App).db.playlistDao())
    private val _items = MutableLiveData<List<PlaylistItem>>()
    val items: LiveData<List<PlaylistItem>> = _items

    init {
        viewModelScope.launch {
            repo.items(playlistId).collectLatest { list ->
                _items.postValue(list)
            }
        }
    }}

class PlaylistDetailViewModelFactory(
    private val app: Application,
    private val playlistId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlaylistDetailViewModel(app, playlistId) as T
    }
}
