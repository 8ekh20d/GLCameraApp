package com.peopleinnet.glcameraapp.gl

import android.content.Context

object ShaderLoader {

    fun loadRaw(context: Context, resId: Int): String {
        return context.resources.openRawResource(resId)
            .bufferedReader()
            .use { it.readText() }
    }
}