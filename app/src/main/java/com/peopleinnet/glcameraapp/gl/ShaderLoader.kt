package com.peopleinnet.glcameraapp.gl

import android.content.Context
import com.peopleinnet.glcameraapp.GLCameraApp

object ShaderLoader {

    private const val TAG = "ShaderLoader"

    /**
     * Loads shader source code from raw resources
     * @param resId Resource ID of the shader file
     * @return Shader source code as String
     * @throws IllegalArgumentException if resource cannot be loaded
     */
    fun loadRaw(resId: Int): String {
        return try {
            GLCameraApp.instance.resources.openRawResource(resId)
                .bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            val errorMsg = "Failed to load shader resource: $resId"
            android.util.Log.e(TAG, errorMsg, e)
            throw IllegalArgumentException(errorMsg, e)
        }
    }
}