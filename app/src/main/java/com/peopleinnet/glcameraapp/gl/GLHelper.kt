package com.peopleinnet.glcameraapp.gl

import android.opengl.GLES11Ext
import android.opengl.GLES20

object GLHelper {

    private const val TAG = "GLHelper"

    fun createOESTexture(): Int {
        val texture = IntArray(1)

        GLES20.glGenTextures(1, texture, 0)
        checkGLError("glGenTextures")

        GLES20.glBindTexture(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            texture[0]
        )
        checkGLError("glBindTexture")

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        checkGLError("glTexParameteri MIN_FILTER")

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        checkGLError("glTexParameteri MAG_FILTER")

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        checkGLError("glTexParameteri WRAP_S")

        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        checkGLError("glTexParameteri WRAP_T")

        return texture[0]
    }

    /**
     * Checks for OpenGL errors and logs them
     * @param operation The name of the GL operation to check
     * @throws RuntimeException if a GL error occurred
     */
    fun checkGLError(operation: String) {
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            val errorMsg = "GL Error after $operation: 0x${Integer.toHexString(error)}"
            android.util.Log.e(TAG, errorMsg)
            throw RuntimeException(errorMsg)
        }
    }
}