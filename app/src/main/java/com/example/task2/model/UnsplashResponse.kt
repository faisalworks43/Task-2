package com.example.task2.model

data class UnsplashResponse(
    val results: List<PhotoResult>
)

data class PhotoResult(
    val id: String,
    val urls: Urls
)

data class Urls(
    val raw: String,
    val full: String,
    val regular: String,
    val small: String,
    val thumb: String
)