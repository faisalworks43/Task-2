package com.example.task2.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File

fun Context.saveImages(imageCapture: ImageCapture) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Use MediaStore for Android 10 and above
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "${System.currentTimeMillis()}.jpg"
            )
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Wallpaper-App")
        }

        val contentResolver = contentResolver
        val imageUri = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )

        imageUri?.let { uri ->
            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                contentResolver,
                uri,
                contentValues
            ).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Image successfully saved
                        Toast.makeText(
                            this@saveImages,
                            "Image Saved.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            this@saveImages,
                            "Failed to save image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    } else {
        // For Android 9 and below, use traditional file saving to app-specific directory
        val wallpaperAppDir = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "Wallpaper-App"
        )
        if (!wallpaperAppDir.exists()) {
            wallpaperAppDir.mkdirs() // Create the folder if it doesn't exist
        }

        // Create file for the captured image
        val file = File(wallpaperAppDir, "${System.currentTimeMillis()}.jpg")

        try {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Image successfully saved
                        Toast.makeText(
                            this@saveImages,
                            "Image Saved.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            this@saveImages,
                            "Failed to save image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }
}