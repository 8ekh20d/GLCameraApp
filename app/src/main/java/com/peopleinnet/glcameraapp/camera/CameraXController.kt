package com.peopleinnet.glcameraapp.camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.peopleinnet.glcameraapp.GLCameraApp
import java.util.concurrent.Executor

class CameraXController(
    private val lifecycleOwner: LifecycleOwner,
    private val surfaceTexture: SurfaceTexture
) {

    private var cameraProvider: ProcessCameraProvider? = null
    private val appContext = GLCameraApp.instance
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(appContext)

    fun start() {
        val future = ProcessCameraProvider.getInstance(appContext)

        future.addListener({
            cameraProvider = future.get()

            val preview = Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build()

            preview.setSurfaceProvider { request ->
                val surface = Surface(surfaceTexture)
                request.provideSurface(surface, mainExecutor) {
                    surface.release()
                }
            }

            // Unbind previous use cases and bind new
            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview
            )

        }, mainExecutor)
    }

    fun stop() {
        cameraProvider?.unbindAll()
    }

    fun release() {
        cameraProvider?.unbindAll()
        cameraProvider = null
    }
}