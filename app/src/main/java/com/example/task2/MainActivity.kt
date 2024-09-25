package com.example.task2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task2.ui.APIScreen
import com.example.task2.ui.CameraScreen
import com.example.task2.ui.ImageViewerScreen
import com.example.task2.ui.SavedScreen
import com.example.task2.ui.StorageScreen
import com.example.task2.ui.theme.Task2Theme
import com.example.task2.viewmodel.GalleryViewModelFactory
import com.example.task2.viewmodel.StorageViewModel
import com.example.task2.viewmodel.StorageViewModelFactory
import com.example.task2.viewmodel.UnSplashViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Task2Theme {
                Scaffold { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen() {

    val context = LocalContext.current
    val navController = rememberNavController()

    val storageViewModel: StorageViewModel = viewModel(factory = StorageViewModelFactory(context))
    val apiViewModel: UnSplashViewModel = viewModel(factory = GalleryViewModelFactory())

    val tabData = getTabList()
    val pagerState = rememberPagerState(pageCount = tabData.size)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val showTabs =
        currentBackStackEntry?.destination?.route != "image_detail_screen/{imagesJson}/{selectedIndex}"

    Column(modifier = Modifier.fillMaxSize()) {
        if (showTabs) {
            TabLayout(tabData, pagerState)
        }

        NavHost(navController = navController, startDestination = "tabs") {
            composable("tabs") {
                TabContent(
                    context,
                    tabData,
                    pagerState,
                    storageViewModel,
                    apiViewModel,
                    navController
                )
            }
            composable(
                route = "image_detail_screen/{imagesJson}/{selectedIndex}",
                arguments = listOf(
                    navArgument("imagesJson") { type = NavType.StringType },
                    navArgument("selectedIndex") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val imagesJson = backStackEntry.arguments?.getString("imagesJson")
                val selectedIndex = backStackEntry.arguments?.getInt("selectedIndex") ?: 0
                ImageViewerScreen(imagesJson, selectedIndex)
            }
        }
    }
}

private fun getTabList(): List<String> {
    return listOf("API", "Storage", "Camera", "Saved")
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(tabData: List<String>, pagerState: PagerState) {

    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        divider = {
            Spacer(modifier = Modifier.height(5.dp))
        },
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                height = 5.dp,
                color = Color.White
            )
        },


        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        tabData.forEachIndexed { index, value ->
            Tab(selected = pagerState.currentPage == index, onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
                text = {
                    Text(text = value)
                })
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    context: Context,
    tabData: List<String>,
    pagerState: PagerState,
    storageViewModel: StorageViewModel,
    apiViewModel: UnSplashViewModel,
    navController: NavHostController
) {
    HorizontalPager(state = pagerState) { index ->
        when (index) {
            0 -> {
                APIScreen(apiViewModel, navController)
            }

            1 -> {
                StorageScreen(storageViewModel, navController)
            }

            2 -> {
                CameraScreen()
            }

            3 -> {
                SavedScreen(storageViewModel)
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview()
@Composable
fun PreviewTab() {
    Task2Theme {
        val tabData = getTabList()
        val pagerState = rememberPagerState(pageCount = tabData.size)
        TabLayout(tabData, pagerState)
    }
}

@Preview()
@Composable
fun PreviewContent() {
    Task2Theme {
        MainScreen()
    }
}
