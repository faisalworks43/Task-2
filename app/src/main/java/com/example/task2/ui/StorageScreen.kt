package com.example.task2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.task2.model.StorageImageModel
import com.example.task2.utils.UriTypeAdapter
import com.example.task2.viewmodel.StorageViewModel
import com.google.gson.GsonBuilder
import java.net.URLEncoder

@Composable
fun StorageScreen(storageViewModel: StorageViewModel, navController: NavController) {

    val context = LocalContext.current

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            val readMediaImagesGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false

            if (readGranted || readMediaImagesGranted) {
                storageViewModel.fetchAllImages()
            } else {
                Toast.makeText(
                    context,
                    "Storage permission required.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
            storageViewModel.fetchAllImages()
        }

        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
            storageViewModel.fetchAllImages()
        }

        else -> {
            LaunchedEffect(Unit) {
                storagePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            }
        }
    }

    val images by storageViewModel.allImages.observeAsState(emptyList())
    val gson = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .create()

    val imagesJson = remember(images) {
        val jsonString = gson.toJson(images) // Use the custom Gson instance
        URLEncoder.encode(jsonString, "UTF-8")
    }


    SetImagesList(ArrayList(images)) { selectedIndex ->
        navController.navigate("image_detail_screen/${imagesJson}/$selectedIndex")
    }

}

@Composable
fun SetImagesList(storageImageItems: ArrayList<StorageImageModel>, onItemClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (storageImageItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No images found")
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                itemsIndexed(items = storageImageItems) { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp, vertical = 5.dp)
                            .clickable { onItemClick(index) }
                    ) {
                        Column(
                            modifier = Modifier
                                .background(Color.LightGray)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter(item.uri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Preview(showSystemUi = true)
@Composable
fun StorageScreenPreview() {
    val storageViewModel = StorageViewModel(LocalContext.current)
    val navController = rememberNavController()
    StorageScreen(storageViewModel, navController)
}
