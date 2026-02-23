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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.peopleinnet.glcameraapp.camera.CameraXController
import com.peopleinnet.glcameraapp.filters.GrayFilter
import com.peopleinnet.glcameraapp.filters.NormalFilter
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer
import com.peopleinnet.glcameraapp.ui.theme.GLCameraAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var renderer: GLCameraRenderer
    private lateinit var glSurfaceView: GLSurfaceView
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

        renderer = GLCameraRenderer(applicationContext)
        renderer.setFilter(GrayFilter(applicationContext))

        enableEdgeToEdge()

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(
                    factory = { context ->
                        glSurfaceView = GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(renderer)
                        }
                        glSurfaceView
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(start = 24.dp, end = 24.dp, bottom = 70.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            Log.e("Gray", "activity click")
                            glSurfaceView.queueEvent {
                                renderer.setFilter(GrayFilter(applicationContext))
                            }
                        }
                    ) {
                        Text("Gray")
                    }

                    Button(
                        onClick = {
                            Log.e("Normal", "activity click")
                            glSurfaceView.queueEvent {
                                renderer.setFilter(NormalFilter(applicationContext))
                            }
                        }
                    ) {
                        Text("Normal")
                    }
                }
            }
        }

        checkPermission()
    }

    private fun startCamera() {
        renderer.onSurfaceTextureReady = { texture ->
            cameraController = CameraXController(
                this,
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

}