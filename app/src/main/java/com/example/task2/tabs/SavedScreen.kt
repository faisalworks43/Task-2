package com.example.task2.tabs

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.task2.model.StorageImageModel
import com.example.task2.viewmodel.StorageViewModel

@Composable
fun SavedScreen(storageViewModel: StorageViewModel, navController: NavHostController) {


    val context = LocalContext.current

    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val readGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false
            val readMediaImagesGranted = permissions[Manifest.permission.READ_MEDIA_IMAGES] ?: false

            if (readGranted || readMediaImagesGranted) {
                storageViewModel.fetchSavedImages()
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
            storageViewModel.fetchSavedImages()
        }

        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
            storageViewModel.fetchSavedImages()
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

    val images by storageViewModel.savedImages.observeAsState(emptyList())

    SetSavedImagesList(ArrayList(images))
}

@Composable
fun SetSavedImagesList(storageImageItems: ArrayList<StorageImageModel>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            itemsIndexed(items = storageImageItems) { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp, vertical = 5.dp)
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

@Preview(showSystemUi = true)
@Composable
fun SavedScreenPreview() {
    val storageViewModel = StorageViewModel(LocalContext.current)
    val navController = rememberNavController()

    SavedScreen(storageViewModel, navController)
}
