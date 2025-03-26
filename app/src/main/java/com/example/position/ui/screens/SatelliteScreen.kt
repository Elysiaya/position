package com.example.position.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.position.ui.model.GnssViewModel
import kotlinx.coroutines.launch


// 定义 Tab 类型
sealed class SatelliteTab(val title: String) {
    data object Status : SatelliteTab("Status")
    data object Skyplot : SatelliteTab("Skyplot")
    data object Measurements : SatelliteTab("Measurements")
}

@Composable
fun SatelliteScreen(viewModel: GnssViewModel = viewModel()
)
{
    val pagerState = rememberPagerState (initialPage = 0, pageCount = {3})
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // 1. 顶部导航栏
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth(),
        ) {
            listOf("Status", "Skyplot", "Measurements").forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(state = pagerState, beyondViewportPageCount = 2) {
            page: Int ->
            when(page){
                0 -> StatusTab(viewModel)
                1 -> SkyplotTab(viewModel)
                2 -> MeasurementsTab(viewModel)
            }
        }
    }
}
