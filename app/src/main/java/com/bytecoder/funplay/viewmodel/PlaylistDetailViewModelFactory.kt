package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PlaylistDetailViewModelFactory(
    private val app: Application,
    private val playlistId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistDetailViewModel::class.java)) {
            val vm = PlaylistDetailViewModel(app)
            vm.setPlaylistId(playlistId)
            @Suppress("UNCHECKED_CAST")
            return vm as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
