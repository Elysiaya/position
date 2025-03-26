package com.example.position.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.position.ui.model.GnssViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SkyplotTab(viewModel: GnssViewModel) {
    val satellitePositions by viewModel.satellitePositions.collectAsState()
//    val gnssStatus by viewModel.gnssStatus.collectAsState()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension * 0.4f

        // 1. 绘制同心圆（仰角刻度）
        drawSkyplotGrid(center, radius)

        // 2. 绘制卫星位置
        satellitePositions.forEach { pos ->
            drawSatellite(
                center = center,
                baseRadius = radius,
                azimuth = pos.azimuth,
                elevation = pos.elevation,
                snr = pos.snr,
                isUsedInFix = pos.usedInFix
            )
            drawSatelliteLabel(
                center = center,
                svid = pos.svid,
                azimuth = pos.azimuth,
                elevation = pos.elevation,
            )
        }
    }
}
//绘制同心圆
private fun DrawScope.drawSkyplotGrid(center: Offset, radius: Float) {
    // 绘制同心圆（仰角 0°-90°）
    listOf(0.2f, 0.5f, 0.8f, 1f).forEach { scale ->
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.3f),
            radius = radius * scale,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }

    // 绘制方位角刻度（0°, 90°, 180°, 270°）
    listOf(0f, 90f, 180f, 270f).forEach { azimuth ->
        val radians = Math.toRadians(azimuth.toDouble())
        val endX = center.x + radius * cos(radians).toFloat()
        val endY = center.y + radius * sin(radians).toFloat()
        drawLine(
            color = Color.Gray,
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 1.dp.toPx()
        )
    }
}

private fun DrawScope.drawSatellite(
    center: Offset,
    baseRadius: Float,
    azimuth: Float,      // 方位角（0°-360°）
    elevation: Float,    // 仰角（0°-90°）
    snr: Float,          // 信噪比（用于大小/颜色）
    isUsedInFix: Boolean // 是否用于定位
) {
    val distanceFromCenter = baseRadius * (1 - elevation / 90f)
    val rad = Math.toRadians(azimuth.toDouble())
    val x = center.x + distanceFromCenter * cos(rad).toFloat()
    val y = center.y + distanceFromCenter * sin(rad).toFloat()

    // 根据信噪比决定卫星点大小和颜色
    val size = (snr / 50f).coerceIn(0.5f, 2f) * 10.dp.toPx()
    val color = when {
        snr > 40 -> Color.Green
        snr > 30 -> Color.Yellow
        else -> Color.Red
    }

    drawCircle(
        color = if (isUsedInFix) color else color.copy(alpha = 0.5f),
        center = Offset(x, y),
        radius = size,
        style = if (isUsedInFix) Fill else Stroke(width = 2.dp.toPx())
    )
}

//绘制卫星标签
private fun DrawScope.drawSatelliteLabel(
    center: Offset,
    svid: Int,
    azimuth: Float,
    elevation: Float
) {
    val distance = size.minDimension * 0.4f * (1 - elevation / 90f)
    val rad = Math.toRadians(azimuth.toDouble())
    val x = center.x + distance * cos(rad).toFloat()
    val y = center.y + distance * sin(rad).toFloat()

    drawContext.canvas.nativeCanvas.apply {
        drawText(
            svid.toString(),
            x + 15.dp.toPx(),
            y,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
//                color = ,
                textSize = 12.sp.toPx()
            }
        )
    }
}
