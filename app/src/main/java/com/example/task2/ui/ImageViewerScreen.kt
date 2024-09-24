package com.example.task2.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.task2.model.PhotoResult
import com.example.task2.model.StorageImageModel
import com.example.task2.utils.UriTypeAdapter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewerScreen(srcJson: String?, selectedIndex: Int) {

    val jsonApi = URLDecoder.decode(srcJson, "UTF-8")
    val srcListType = object : TypeToken<List<PhotoResult>>() {}.type
    val apiImagesList: List<PhotoResult> = Gson().fromJson<List<PhotoResult>?>(jsonApi, srcListType)
        .filter {
            it.id != null && it.urls != null
        }

    val galleryImagesList = GsonBuilder()
        .registerTypeAdapter(Uri::class.java, UriTypeAdapter())
        .create().fromJson(jsonApi, Array<StorageImageModel>::class.java)
        .toList()
        .filter {
            it.uri != null && it.uri.toString().isNotBlank() &&
                    !it.path.isNullOrBlank()
        }

    if (galleryImagesList.isNotEmpty()) {
        val pagerState = rememberPagerState(
            pageCount = galleryImagesList.size,
            initialPage = selectedIndex
        )

        SetGalleryImagesPager(galleryImagesList, pagerState)
    }

    if (apiImagesList.isNotEmpty()) {
        val pagerState = rememberPagerState(
            pageCount = apiImagesList.size,
            initialPage = selectedIndex
        )

        SetApiImagesPager(apiImagesList, pagerState)
    }

    if (apiImagesList.isEmpty() && galleryImagesList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(text = "No images found")
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SetGalleryImagesPager(images: List<StorageImageModel>?, pagerState: PagerState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val image = images?.get(page)
            Image(
                painter = rememberImagePainter(image?.uri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${images?.size ?: 0}",
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SetApiImagesPager(images: List<PhotoResult>, pagerState: PagerState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val image = images?.get(page)
            Image(
                painter = rememberImagePainter(image?.urls?.regular),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "${pagerState.currentPage + 1}/${images?.size ?: 0}",
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ImageViewerScreenPreview() {
    ImageViewerScreen(null, selectedIndex = 0)
}