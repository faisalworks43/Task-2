package com.example.task2.viewmodel

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.task2.model.StorageImageModel
import com.example.task2.utils.SAVED_FOLDER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StorageViewModel(private val context: Context) : ViewModel() {

    private val _allImages = MutableLiveData<List<StorageImageModel>>()
    val allImages: LiveData<List<StorageImageModel>> = _allImages

    private val _savedImages = MutableLiveData<List<StorageImageModel>>()
    val savedImages: LiveData<List<StorageImageModel>> = _savedImages

    fun fetchAllImages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageList = getAllImages(context)

                Log.e("StorageViewModel", "Fetching all images ${imageList.size}")

                _allImages.postValue(imageList)
            } catch (e: Exception) {
                Log.e("StorageViewModel", "Error fetching all images", e)
            }
        }
    }

    fun fetchSavedImages() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imageList = getSavedImages(context)

                Log.e("StorageViewModel", "Fetching saved images ${imageList.size}")

                _savedImages.postValue(imageList)
            } catch (e: Exception) {
                Log.e("StorageViewModel", "Error fetching saved images", e)
            }
        }
    }

    private fun getAllImages(context: Context): List<StorageImageModel> {
        val imageList = mutableListOf<StorageImageModel>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )

        query?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                imageList.add(
                    StorageImageModel(
                        uri = uri,
                        name = name,
                        path = path
                    )
                )
            }
        }
        return imageList
    }

    private fun getSavedImages(context: Context): List<StorageImageModel> {
        val imageList = mutableListOf<StorageImageModel>()

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = context.contentResolver.query(
            collection, projection, null, null, sortOrder
        )

        query?.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val name =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val path =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))

                if (path.contains(SAVED_FOLDER)) {
                    imageList.add(
                        StorageImageModel(
                            uri = uri,
                            name = name,
                            path = path
                        )
                    )
                }
            }
        }

        return imageList
    }
}

class StorageViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
