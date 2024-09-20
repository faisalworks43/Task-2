package com.example.task2.ui

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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewerScreen(images: List<StorageImageModel>?, apiImages: List<PhotoResult>?, selectedIndex: Int) {

    if (!images.isNullOrEmpty()) {

        val pagerState = rememberPagerState(
            pageCount = images?.size ?: 0,
            initialPage = selectedIndex
        )

        SetImagesPager(images, pagerState)
    } else if (!apiImages.isNullOrEmpty()) {

        val pagerState = rememberPagerState(
            pageCount = apiImages?.size ?: 0,
            initialPage = selectedIndex
        )

        SetImagesPagerX(apiImages, pagerState)
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SetImagesPager(images: List<StorageImageModel>?, pagerState: PagerState) {
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
fun SetImagesPagerX(images: List<PhotoResult>, pagerState: PagerState) {
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
    ImageViewerScreen(images = null, null, selectedIndex = 0)
}