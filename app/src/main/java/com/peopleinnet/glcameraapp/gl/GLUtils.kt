package com.peopleinnet.glcameraapp.gl

import android.opengl.GLES11Ext
import android.opengl.GLES20

object GLUtils {

    fun createOESTexture(): Int {
        val texture = IntArray(1)
        GLES20.glGenTextures(1, texture, 0)

        GLES20.glBindTexture(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            texture[0]
        )

        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )

        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )

        return texture[0]
    }
}