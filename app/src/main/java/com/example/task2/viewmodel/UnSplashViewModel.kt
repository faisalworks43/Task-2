package com.example.task2.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.task2.model.PhotoResult
import com.example.task2.repo.UnsplashRepository
import kotlinx.coroutines.launch

class UnSplashViewModel(private val repository: UnsplashRepository?) : ViewModel() {

    private val _photos = mutableStateOf<List<PhotoResult>>(emptyList())
    val photos: State<List<PhotoResult>> = _photos

    private val _loading = mutableStateOf(false)
    val isLoading: State<Boolean> = _loading

    fun searchPhotos(query: String, page: Int, perPage: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository?.searchPhotos(query, page, perPage)
                response?.let {
                    _photos.value = it.results
                }
            } catch (e: Exception) {
                _loading.value = false
            } finally {
                _loading.value = false
            }
        }
    }
}

class GalleryViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UnSplashViewModel::class.java)) {
            return UnSplashViewModel(UnsplashRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}