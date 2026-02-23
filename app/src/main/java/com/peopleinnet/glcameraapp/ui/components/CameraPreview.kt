package com.peopleinnet.glcameraapp.ui.components

import android.opengl.GLSurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.peopleinnet.glcameraapp.gl.GLCameraRenderer

@Composable
fun CameraPreview(
    renderer: GLCameraRenderer,
    onSurfaceReady: (GLSurfaceView) -> Unit
) {
    AndroidView(
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(2)
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                onSurfaceReady(this)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}