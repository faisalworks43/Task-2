package com.example.task2.api

import com.example.task2.model.UnsplashResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val CLIENT_ID = "mXLsVXW0q8EC6JqjEfZYFbTvk334zd4RnCpoSEd6csY"

interface UnsplashApiService {
    @Headers("Accept-Version: v1", "Authorization: Client-ID $CLIENT_ID")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): UnsplashResponse
}