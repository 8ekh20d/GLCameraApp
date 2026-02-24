package com.peopleinnet.glcameraapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun MetricsOverlay(
    selectedFilter: String,
    frameTime: Float,
    avgFrameTime: Float,
    minFrameTime: Float,
    maxFrameTime: Float,
    modifier: Modifier = Modifier
) {
    fun msToFps(ms: Float) = if (ms > 0f) 1000f / ms else 0f

    val currentFps = msToFps(frameTime)
    val avgFps = msToFps(avgFrameTime)
    val minFps = msToFps(minFrameTime)
    val maxFps = msToFps(maxFrameTime)

    Text(
        text =
            "Filter: $selectedFilter\n" +
                    "Frame: %.2f ms (~%.0f FPS)\n".format(frameTime, currentFps) +
                    "Avg:   %.2f ms (~%.0f FPS)\n".format(avgFrameTime, avgFps) +
                    "Min:   %.2f ms (~%.0f FPS)\n".format(minFrameTime, minFps) +
                    "Max:   %.2f ms (~%.0f FPS)".format(maxFrameTime, maxFps),
        color = Color.White,
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(top = 34.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
    )
}