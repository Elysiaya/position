package com.example.position.ui.screens

import android.location.GnssStatus
import android.location.GnssStatus.CONSTELLATION_BEIDOU
import android.location.GnssStatus.CONSTELLATION_GALILEO
import android.location.GnssStatus.CONSTELLATION_GLONASS
import android.location.GnssStatus.CONSTELLATION_GPS
import android.location.GnssStatus.CONSTELLATION_IRNSS
import android.location.GnssStatus.CONSTELLATION_QZSS
import android.location.GnssStatus.CONSTELLATION_SBAS
import android.location.GnssStatus.CONSTELLATION_UNKNOWN
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.position.ui.model.GnssViewModel
import com.example.position.ui.theme.PositionTheme

@Composable
fun StatusTab(viewModel: GnssViewModel = viewModel()
)
{
    val gnssStatus by viewModel.gnssStatus.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startListening()
    }

    Column(modifier = Modifier.padding(10.dp)) {
        if (gnssStatus != null) {
            SatelliteInfoList(gnssStatus!!)
        } else {
            Text("No GNSS data available")
        }
    }
}
@Composable
fun SatelliteInfoList(gnssStatus:GnssStatus){
    // 表头
    SatelliteTableHeader()
    LazyColumn ()
    {
        items (gnssStatus.satelliteCount)
        {
                satelliteIndex->
            val svid = gnssStatus.getSvid(satelliteIndex)
            val GNSStype = GetGNSSType(gnssStatus.getConstellationType(satelliteIndex))
            val Freq = (gnssStatus.getCarrierFrequencyHz(satelliteIndex)/1000000).format3Decimal()
            val NavMsg = gnssStatus.hasEphemerisData(satelliteIndex).toString()
            val Elev = gnssStatus.getElevationDegrees(satelliteIndex).format2Decimal()
            val Azim = gnssStatus.getAzimuthDegrees(satelliteIndex).format2Decimal()
            SatelliteInfoRow(svid, GNSStype, Freq, NavMsg, Elev, Azim)
        }

    }

//    Column (){
//        (0 until gnssStatus.satelliteCount).forEach()
//        {
//                satelliteIndex->
//            val svid = gnssStatus.getSvid(satelliteIndex)
//            val GNSStype = GetGNSSType(gnssStatus.getConstellationType(satelliteIndex))
//            val Freq = (gnssStatus.getCarrierFrequencyHz(satelliteIndex)/1000000).format3Decimal()
//            val NavMsg = gnssStatus.hasEphemerisData(satelliteIndex).toString()
//            val Elev = gnssStatus.getElevationDegrees(satelliteIndex).format2Decimal()
//            val Azim = gnssStatus.getAzimuthDegrees(satelliteIndex).format2Decimal()
//            SatelliteInfoRow(svid, GNSStype, Freq, NavMsg, Elev, Azim)
//        }
//    }
}

// 表头组件
@Composable
fun SatelliteTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell("ID", modifier = Modifier.weight(0.1f))
        TableCell("GNSS", modifier = Modifier.weight(0.1f))
        TableCell("Freq(MHz)", modifier = Modifier.weight(0.2f))
        TableCell("NavMsg", modifier = Modifier.weight(0.2f))
        TableCell("Elev(°)", modifier = Modifier.weight(0.15f))
        TableCell("Azim(°)", modifier = Modifier.weight(0.15f))
    }
    HorizontalDivider(thickness = 1.dp, color = Color.Gray)
}
// 表格行组件
@Composable
fun SatelliteInfoRow(
    ID: Int,
    GNSS: String,
    Freq: String,
    NavMsg: String,
    Elev: String,
    Azim: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(ID.toString(),modifier = Modifier.weight(0.1f))
        TableCell(GNSS, modifier = Modifier.weight(0.1f))
        TableCell(Freq, modifier = Modifier.weight(0.2f))
        TableCell(NavMsg,modifier = Modifier.weight(0.2f))
        TableCell(Elev, modifier = Modifier.weight(0.15f))
        TableCell(Azim, modifier = Modifier.weight(0.15f))
    }
    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
}

// 通用表格单元格
@Composable
fun TableCell(text: String, modifier: Modifier)
{
    Box (modifier = modifier){
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
    }

}

fun GetGNSSType(satelliteIndex:Int):String
{
    when(satelliteIndex)
    {
//        中国国旗
        CONSTELLATION_BEIDOU -> return "\uD83C\uDDE8\uD83C\uDDF3"
//        美国国旗
        CONSTELLATION_GPS -> return "\uD83C\uDDFA\uD83C\uDDF2"
//        欧盟旗帜
        CONSTELLATION_GALILEO -> return "\uD83C\uDDEA\uD83C\uDDFA"
//        俄罗斯
        CONSTELLATION_GLONASS -> return "\uD83C\uDDF7\uD83C\uDDFA"
//        日本
        CONSTELLATION_QZSS -> return "\uD83C\uDDEF\uD83C\uDDF5"
//        印度
        CONSTELLATION_IRNSS -> return "\uD83C\uDDEE\uD83C\uDDF3"

        CONSTELLATION_SBAS -> return "SBAS"
        CONSTELLATION_UNKNOWN -> return "unknown"
        else -> {
            return "unknown"
        }

    }

}
@Preview(showBackground = true)
@Composable
fun SatelliteinfoPreview(){
    PositionTheme()
    {
        SatelliteInfoRow(1,"GPS","F1","2000","36","49")
    }
}

@Preview(showBackground = true)
@Composable
fun SatelliteScreenPreview(){
    PositionTheme {
        SatelliteScreen(viewModel = GnssViewModel(context = LocalContext.current))
    }
}