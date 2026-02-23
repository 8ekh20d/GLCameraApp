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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.peopleinnet.glcameraapp.camera.CameraXController
import com.peopleinnet.glcameraapp.filters.GrayFilter
import com.peopleinnet.glcameraapp.filters.NormalFilter
import com.peopleinnet.glcameraapp.filters.SepiaFilter
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer
import com.peopleinnet.glcameraapp.ui.FilterSelector
import com.peopleinnet.glcameraapp.ui.theme.GLCameraAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var renderer: GLCameraRenderer
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
            }
        }

        checkPermission()
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