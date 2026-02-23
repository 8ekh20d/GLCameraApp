package com.peopleinnet.glcameraapp

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.peopleinnet.glcameraapp.camera.CameraXController
import com.peopleinnet.glcameraapp.filters.GrayFilter
import com.peopleinnet.glcameraapp.filters.NormalFilter
import com.peopleinnet.glcameraapp.filters.SepiaFilter
import com.peopleinnet.glcameraapp.gl.FrameMetricsListener
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer
import com.peopleinnet.glcameraapp.ui.components.MainScreen
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
            MainScreen(
                viewModel = viewModel,
                renderer = renderer,
                onGLSurfaceReady = { glView ->
                    glSurfaceView = glView
                },
                onFilterSelected = { filter ->

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