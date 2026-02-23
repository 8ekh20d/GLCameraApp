package com.peopleinnet.glcameraapp.ui.components

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer
import com.peopleinnet.glcameraapp.viewmodel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onFilterSelected: (String) -> Unit,
    onGLSurfaceReady: (GLSurfaceView) -> Unit,
    renderer: GLCameraRenderer
) {
    var selectedFilter by remember { mutableStateOf("Normal") }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            renderer = renderer,
            onSurfaceReady = onGLSurfaceReady
        )

        if (viewModel.showMetrics) {
            MetricsOverlay(
                selectedFilter = selectedFilter,
                frameTime = viewModel.frameTimeMs,
                avgFrameTime = viewModel.avgFrameTimeMs,
                minFrameTime = viewModel.minFrameTimeMs,
                maxFrameTime = viewModel.maxFrameTimeMs,
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        FilterSection(
            selectedFilter = selectedFilter,
            onFilterSelected = { filter ->
                selectedFilter = filter
                onFilterSelected(filter)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        MetricsToggleButton(
            showMetrics = viewModel.showMetrics,
            onToggle = { viewModel.toggleMetrics() }
        )
    }
}