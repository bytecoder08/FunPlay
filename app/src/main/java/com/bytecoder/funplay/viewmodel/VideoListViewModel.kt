package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.bytecoder.funplay.data.model.Video
import com.bytecoder.funplay.data.repository.VideoRepository
import kotlinx.coroutines.launch

class VideoListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = VideoRepository(app)
    private val _videos = MutableLiveData<List<Video>>(emptyList())
    val videos: LiveData<List<Video>> = _videos

    fun load() = viewModelScope.launch {
        _videos.value = repo.loadLocalVideos()
    }
}
