package com.example.task2.tabs

import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.task2.model.StorageImageModel
import com.example.task2.utils.ImageLoaderX

@Composable
fun StorageScreen() {

    StorageImagesList()

}

@Composable
fun StorageImagesList() {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        ImageLoaderX(context).apply {
            mSortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"
        }.getAllImages(
            onImageListSuccess = {
                Log.e("TAG--->", "StorageImagesList: ${it.size}")
//                SetImagesList(it)
            })
    }
}

@Composable
fun SetImagesList(storageImageItems: ArrayList<StorageImageModel>) {
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
fun StorageScreenPreview() {
    StorageScreen()
}
