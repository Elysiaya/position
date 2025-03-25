package com.example.position.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.position.ui.model.GnssViewModel
import com.example.position.ui.theme.PositionTheme
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun PositionScreen(modifier: Modifier = Modifier,
                   viewModel: GnssViewModel = viewModel()
) {
    val context = LocalContext.current
    val gnssStatus by viewModel.gnssStatus.collectAsState()
    var currentDateTime by remember { mutableStateOf<LocalDateTime>(LocalDateTime.now()) }
    val locationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    val hasPermission = remember {
        ContextCompat.checkSelfPermission(context, locationPermission) ==
                PackageManager.PERMISSION_GRANTED
    }
    val location by viewModel.location.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L) // 每秒更新一次
            currentDateTime = LocalDateTime.now()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.startListening()
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        C1(
            "经纬度",
            "${location?.latitude}\n${location?.longitude}",
            modifier = Modifier.fillMaxWidth()
        )
        C2(title1 = "海拔",content1 = location?.altitude.format2Decimal(),
            title2 = "速度", content2 = location?.speed.format2Decimal())
        C2(title1 = "卫星数",content1 = "${gnssStatus?.satelliteCount}",
            title2 = "HDOP",content2 = location?.accuracy.format2Decimal())

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        C2(title1 = "日期", content1 = currentDateTime.format(dateFormatter),
            title2 = "时间", content2 = currentDateTime.format(timeFormatter))

//        C2()
//        C2()
        if (hasPermission)
        {
            Text("Location permission granted!")
        }
        else
        {
            Text("Location permission denied.")
        }
    }
}

// 定义扩展函数
fun Double?.format2Decimal(): String {
    return this?.let { "%.2f".format(it) } ?: "N/A"
}

fun Float?.format2Decimal(): String {
    return this?.let { "%.2f".format(it) } ?: "N/A"
}
fun Float?.format3Decimal(): String {
    return this?.let { "%.3f".format(it) } ?: "N/A"
}

@Composable
fun C1(
    title: String = "Title",
    content: String = "Context",
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(3.dp)
            .border(2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
    ) {

        Text(text = title, fontSize = 15.sp, modifier = Modifier.padding(top = 6.dp))
        Text(text = content, fontSize = 25.sp, modifier = Modifier.padding(bottom = 5.dp))
    }
}

@Composable
fun C2(
    title1: String = "Title", content1: String = "Context",
    title2: String = "Title2", content2: String = "Context",
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        C1(title1, content1, modifier = Modifier.weight(1f))
        C1(title2, content2, modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true)
@Composable
fun PositionScreenPreview() {
    PositionTheme {
}}