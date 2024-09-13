package com.example.task2.repo

import com.example.task2.api.RetrofitInstance
import com.example.task2.model.UnsplashResponse

object UnsplashRepository {
    suspend fun searchPhotos(query: String, page: Int, perPage: Int): UnsplashResponse {
        return RetrofitInstance.api.searchPhotos(query, page, perPage)
    }
}