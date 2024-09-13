package com.example.task2.model

import android.net.Uri

data class StorageImageModel(
    var uri: Uri,
    val imageId: Long,
    val name: String,
    val title: String,
    val path: String,
    val size: Long,
    val modified: Long,
    val dateAdded: Long,
)