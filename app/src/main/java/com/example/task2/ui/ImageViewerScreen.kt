package com.example.task2.ui

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.task2.model.PhotoResult
import com.example.task2.model.StorageImageModel
import com.example.task2.utils.UriTypeAdapter
import com.example.task2.utils.getBitmapFromUri
import com.example.task2.utils.saveImageToStorage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.net.URLDecoder

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageViewerScreen(srcJson: String?, selectedIndex: Int) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentPosition: Int by remember { mutableIntStateOf(selectedIndex) }

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

    val isFromApi = apiImagesList.isNotEmpty()
    val isFromGallery = galleryImagesList.isNotEmpty()

    val pagerState = rememberPagerState(
        pageCount = if (isFromGallery) galleryImagesList.size else apiImagesList.size,
        initialPage = selectedIndex
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            currentPosition = page
        }
    }

    if (isFromGallery) {
        SetGalleryImagesPager(galleryImagesList, pagerState)
    } else if (isFromApi) {
        SetApiImagesPager(apiImagesList, pagerState)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(text = "No images found")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                handleSaveOrCompress(
                    context,
                    scope,
                    currentPosition,
                    apiImagesList,
                    galleryImagesList,
                    compress = false
                )
            }) {
                Text(text = "Save")
            }
            Button(onClick = {
                handleSaveOrCompress(
                    context,
                    scope,
                    null,
                    apiImagesList,
                    galleryImagesList,
                    compress = false
                )
            }) {
                Text(text = "Save All")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                handleSaveOrCompress(
                    context,
                    scope,
                    currentPosition,
                    apiImagesList,
                    galleryImagesList,
                    compress = true
                )
            }) {
                Text(text = "Compress")
            }
            Button(onClick = {
                handleSaveOrCompress(
                    context,
                    scope,
                    null,
                    apiImagesList,
                    galleryImagesList,
                    compress = true
                )
            }) {
                Text(text = "Compress All")
            }
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

fun handleSaveOrCompress(
    context: Context,
    scope: CoroutineScope,
    position: Int?,
    apiImagesList: List<PhotoResult>,
    galleryImagesList: List<StorageImageModel>,
    compress: Boolean
) {
    val imagesToProcess = when {
        position != null && apiImagesList.isNotEmpty() -> listOf(apiImagesList[position])
        position != null && galleryImagesList.isNotEmpty() -> listOf(galleryImagesList[position])
        apiImagesList.isNotEmpty() -> apiImagesList
        galleryImagesList.isNotEmpty() -> galleryImagesList
        else -> emptyList()
    }

    imagesToProcess.forEach { image ->
        scope.launch {
            val bitmap = when (image) {
                is PhotoResult -> getBitmapFromUri(context, image.urls?.regular)
                is StorageImageModel -> getBitmapFromUri(context, image.uri)
                else -> null
            }
            bitmap?.let {
                context.saveImageToStorage(it, compress)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ImageViewerScreenPreview() {
    ImageViewerScreen(null, selectedIndex = 0)
}