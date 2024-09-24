package com.example.task2.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.task2.viewmodel.UnSplashViewModel
import com.google.gson.Gson
import java.net.URLEncoder

@Composable
fun APIScreen(apiViewModel: UnSplashViewModel, navController: NavHostController) {

    LaunchedEffect(Unit) {
        apiViewModel.searchPhotos("Nature", 3, 20)
    }

    val photos by apiViewModel.photos
    val gson = Gson()

    val apiImagesJson = remember(photos) {
        val jsonString = gson.toJson(photos)
        URLEncoder.encode(jsonString, "UTF-8")
    }

    UnSplashGalleryList(apiViewModel) { selectedIndex ->
        navController.navigate("image_detail_screen/${apiImagesJson}/$selectedIndex")
    }

}

@Composable
fun UnSplashGalleryList(
    unSplashViewModel: UnSplashViewModel,
    onImageClick: (Int) -> Unit
) {
    val photos by unSplashViewModel.photos
    val isLoading by unSplashViewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
        } else {
            if (photos.isEmpty()) {
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
                    itemsIndexed(items = photos) { index, item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 5.dp, vertical = 5.dp)
                                .clickable { onImageClick(index) } // Handle image click
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(Color.LightGray)
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Image(
                                    painter = rememberImagePainter(item.urls?.regular),
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
}

@Preview(showSystemUi = true)
@Composable
fun APIScreenPreview() {
    val apiViewModel = UnSplashViewModel(null)
    val navController = rememberNavController()

    APIScreen(apiViewModel, navController)
}
