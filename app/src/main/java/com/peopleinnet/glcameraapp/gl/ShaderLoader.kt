package com.peopleinnet.glcameraapp.gl

import android.content.Context
import com.peopleinnet.glcameraapp.GLCameraApp

object ShaderLoader {

    fun loadRaw(resId: Int): String {
        return GLCameraApp.instance.resources.openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }
    }
}