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

    private var currentPage = 1
    private var isLastPage = false

    fun searchPhotos(query: String, page: Int, perPage: Int) {
        if (isLastPage || isLoading.value) return
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository?.searchPhotos(query, page, perPage)
                response?.let {
                    _photos.value += it.results
                    currentPage++
                    if (it.results.size < perPage) {
                        isLastPage = true
                    }
                }
            } catch (_: Exception) {
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadMorePhotos(query: String, perPage: Int) {
        searchPhotos(query, currentPage, perPage)
    }

    fun resetPagination() {
        currentPage = 1
        isLastPage = false
        _photos.value = emptyList()
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