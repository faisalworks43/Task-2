package com.example.task2.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.task2.model.StorageImageModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageLoaderX(var mContext: Context) {

    private var mJob: Job = Job()

    var mSelection: String? = null
    var mSelectionArgs: Array<String>? = null
    var mSortOrder: String? = null

    var mFilterImageExt: Array<String>? = null

    private val mProjection: Array<String> by lazy {
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
    }

    fun getAllImages(
        onImageListSuccess: ((imageList: ArrayList<StorageImageModel>) -> Unit)? = null,
        onFailed: ((error: String) -> Unit)? = null,
    ) {
        mJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageList = ArrayList<StorageImageModel>()

                val collection =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL
                        )
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                val query = mContext.contentResolver.query(
                    collection,
                    mProjection,
                    mSelection,
                    mSelectionArgs,
                    mSortOrder
                )

                query?.use { cursor ->

                    while (cursor.moveToNext()) {

                        val imageId: Long = cursor.getColumnLong(MediaStore.Images.Media._ID)
                        val name: String =
                            cursor.getColumnString(MediaStore.Images.Media.DISPLAY_NAME)
                        val title: String = cursor.getColumnString(MediaStore.Images.Media.TITLE)
                        val path: String = cursor.getColumnString(MediaStore.Images.Media.DATA)
                        val size: Long = cursor.getColumnLong(MediaStore.Images.Media.SIZE)
                        val modified: Long =
                            cursor.getColumnLong(MediaStore.Images.Media.DATE_MODIFIED)
                        val dateAdded: Long =
                            cursor.getColumnLong(MediaStore.Images.Media.DATE_ADDED)
                        val folderId: String =
                            cursor.getColumnString(MediaStore.Images.Media.BUCKET_ID)
                        var folderName: String =
                            cursor.getColumnString(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                        if (folderName.isEmpty()) folderName = "Internal Storage"

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId
                        )

                        val imageItem = StorageImageModel(
                            uri = contentUri,
                            imageId = imageId,
                            name = name,
                            title = title,
                            path = path,
                            size = size,
                            modified = modified,
                            dateAdded = dateAdded,
                        )


                        fun addDataToLists() {
                            imageList += imageItem
                        }

                        if (mFilterImageExt != null) {
                            val ext = File(path).extension
                            if (mFilterImageExt!!.contains(ext)) {
                                addDataToLists()
                            }
                        } else {
                            addDataToLists()
                        }

                    }
                }

                withContext(Dispatchers.Main) {
                    onImageListSuccess?.invoke(imageList)
                }


            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailed?.invoke(e.message ?: "")
                }
            }
        }

    }

    fun Cursor.getColumnString(mediaColumn: String): String =
        getString(getColumnIndexOrThrow(mediaColumn)) ?: ""

    fun Cursor.getColumnLong(mediaColumn: String): Long =
        getLong(getColumnIndexOrThrow(mediaColumn))

    fun onDestroyLoader() {
        mJob.cancel()
    }


}