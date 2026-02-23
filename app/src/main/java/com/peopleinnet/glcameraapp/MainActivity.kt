package com.peopleinnet.glcameraapp

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.peopleinnet.glcameraapp.camera.CameraXController
import com.peopleinnet.glcameraapp.filters.GrayFilter
import com.peopleinnet.glcameraapp.filters.NormalFilter
import com.peopleinnet.glcameraapp.filters.SepiaFilter
import com.peopleinnet.glcameraapp.gl.FrameMetricsListener
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer
import com.peopleinnet.glcameraapp.ui.components.FilterSelector
import com.peopleinnet.glcameraapp.ui.theme.GLCameraAppTheme
import com.peopleinnet.glcameraapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var renderer: GLCameraRenderer
    private lateinit var viewModel: MainViewModel
    private var glSurfaceView: GLSurfaceView? = null
    private var cameraController: CameraXController? = null
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    Toast.makeText(
                        this,
                        "Permission permanently denied. Please enable it in settings.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission denied.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        renderer = GLCameraRenderer()
        viewModel = MainViewModel()

        enableEdgeToEdge()

        setContent {
            var selectedFilter by remember { mutableStateOf("Normal") }

            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(
                    factory = { context ->
                        GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(renderer)
                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                            glSurfaceView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (viewModel.showMetrics) {

                    fun msToFps(ms: Float): Float {
                        return if (ms > 0f) 1000f / ms else 0f
                    }

                    val currentFps = msToFps(viewModel.frameTimeMs)
                    val avgFps = msToFps(viewModel.avgFrameTimeMs)
                    val minFps = msToFps(viewModel.minFrameTimeMs)
                    val maxFps = msToFps(viewModel.maxFrameTimeMs)

                    Text(
                        text =
                            "Filter: $selectedFilter\n" +

                                    "Frame: %.2f ms (~%.0f FPS)\n"
                                        .format(viewModel.frameTimeMs, currentFps) +

                                    "Avg:   %.2f ms (~%.0f FPS)\n"
                                        .format(viewModel.avgFrameTimeMs, avgFps) +

                                    "Min:   %.2f ms (~%.0f FPS)\n"
                                        .format(viewModel.minFrameTimeMs, minFps) +

                                    "Max:   %.2f ms (~%.0f FPS)"
                                        .format(viewModel.maxFrameTimeMs, maxFps),

                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp, bottom = 70.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    FilterSelector(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            selectedFilter = filter

                            glSurfaceView?.queueEvent {
                                when (filter) {
                                    "Gray" -> renderer.setFilter(GrayFilter())
                                    "Sepia" -> renderer.setFilter(SepiaFilter())
                                    "Normal" -> renderer.setFilter(NormalFilter())
                                }
                            }
                        }
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(top = 32.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {

                    IconButton(
                        onClick = { viewModel.toggleMetrics() }
                    ) {
                        Icon(
                            imageVector = if (viewModel.showMetrics)
                                Icons.Default.Info
                            else
                                Icons.Outlined.Info,
                            contentDescription = "Toggle Metrics",
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }

        checkPermission()
        updateFrameTime()

    }

    private fun updateFrameTime() {
        renderer.frameMetricsListener = object : FrameMetricsListener {
            override fun onFrameProcessed(frameTimeMs: Float) {
                viewModel.updateFrameTime(frameTimeMs)
            }
        }
    }

    private fun startCamera() {
        renderer.onSurfaceTextureReady = { texture ->
            cameraController = CameraXController(
                this,
                texture
            )
            cameraController?.start()
        }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startCamera()
            }

            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        cameraController?.start()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        cameraController?.stop()
        glSurfaceView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        cameraController?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraController?.release()
        cameraController = null

        // Release renderer resources safely
        glSurfaceView?.queueEvent {
            renderer.release()
        }
    }

}