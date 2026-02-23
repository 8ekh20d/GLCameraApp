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

class CameraXController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val surfaceTexture: SurfaceTexture
) {

    fun start() {
        val future = ProcessCameraProvider.getInstance(context)

        future.addListener({
            val provider = future.get()

            val preview = Preview.Builder()
                .setTargetRotation(Surface.ROTATION_0)
                .build()

            preview.setSurfaceProvider { request ->
                val surface = Surface(surfaceTexture)
                request.provideSurface(
                    surface,
                    ContextCompat.getMainExecutor(context)
                ) {}
            }

            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview
            )

        }, ContextCompat.getMainExecutor(context))
    }
}