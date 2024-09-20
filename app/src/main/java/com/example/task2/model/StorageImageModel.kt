package com.example.task2.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageImageModel(
    var uri: Uri,
    val name: String,
    val path: String
) : Parcelable