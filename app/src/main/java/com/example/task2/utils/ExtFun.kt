package com.example.task2.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
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

                saveImageToStorage(bitmap) { absolutePath ->
                    Toast.makeText(
                        this@saveImagesX,
                        "Image saved at $absolutePath",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )
}
fun Context.saveImageToStorage(
    bitmap: Bitmap,
    onSavedSuccess: ((String?) -> Unit)
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
            onSavedSuccess(absolutePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outImage)

            Toast.makeText(
                this@saveImageToStorage,
                "Image Saved.",
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
