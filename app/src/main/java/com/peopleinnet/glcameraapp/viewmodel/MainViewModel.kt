package com.peopleinnet.glcameraapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainViewModel {

    var frameTimeMs by mutableStateOf(0f)
        private set

    var avgFrameTimeMs by mutableStateOf(0f)
        private set

    var minFrameTimeMs by mutableStateOf(Float.MAX_VALUE)
        private set

    var maxFrameTimeMs by mutableStateOf(0f)
        private set

    var showMetrics by mutableStateOf(false)
        private set

    private var frameCount = 0
    private var totalFrameTime = 0f

    fun updateFrameTime(time: Float) {
        frameTimeMs = time

        // Average
        frameCount++
        totalFrameTime += time
        avgFrameTimeMs = totalFrameTime / frameCount

        // Min
        if (time < minFrameTimeMs) {
            minFrameTimeMs = time
        }

        // Max
        if (time > maxFrameTimeMs) {
            maxFrameTimeMs = time
        }
    }

    fun toggleMetrics() {
        showMetrics = !showMetrics

        if (showMetrics) {
            frameCount = 0
            totalFrameTime = 0f
            minFrameTimeMs = Float.MAX_VALUE
            maxFrameTimeMs = 0f
        }
    }

    fun msToFps(ms: Float): Float {
        return if (ms > 0f) 1000f / ms else 0f
    }
}