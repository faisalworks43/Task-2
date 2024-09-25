package com.example.task2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.File

const val SAVED_FOLDER = "Wallpaper-App"

fun Context.saveImagesX(imageCapture: ImageCapture) {
    val outputDirectory = cacheDir
    val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(this),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@saveImagesX, "Error capturing image", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                val bitmap = BitmapFactory.decodeFile(savedUri.toFile().absolutePath)

                saveImageToStorage(bitmap)
            }
        }
    )
}

fun Context.saveImageToStorage(
    bitmap: Bitmap,
    doCompress: Boolean = false
) {
    val name = "Image_${System.currentTimeMillis()}.jpg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$SAVED_FOLDER")
        }
    }

    val resolver = contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let {
        val outputStream = resolver.openOutputStream(it)
        outputStream?.use { outImage ->
            val absolutePath = getImagePathFromURI(it, this)
            val quality = if (doCompress) 50 else 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outImage)

            val message = if (doCompress) "Compressed Image saved at $absolutePath"
            else "Image saved at $absolutePath"

            Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


fun getImagePathFromURI(uri: Uri, context: Context): String? {
    var filePath: String? = null
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
        }
    }
    return filePath
}

suspend fun getBitmapFromUri(context: Context, imageUri: Any?): Bitmap? {
    if (imageUri == null) return null

    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(imageUri)
        .allowHardware(false) // Important to get Bitmap
        .build()

    val result = (loader.execute(request) as? SuccessResult)?.drawable
    return (result as? BitmapDrawable)?.bitmap
}