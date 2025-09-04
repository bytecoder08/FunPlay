package com.bytecoder.funplay.viewmodel

import android.app.Application
import androidx.lifecycle.*
import android.net.Uri
import com.bytecoder.funplay.data.repository.FileRepository
import kotlinx.coroutines.launch

class ExplorerViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = FileRepository(app)
    private val _entries = MutableLiveData<List<Pair<Boolean,String>>>(emptyList())
    val entries: LiveData<List<Pair<Boolean,String>>> = _entries

    fun list(uri: Uri) = viewModelScope.launch {
        _entries.value = repo.listChildren(uri)
    }
}
