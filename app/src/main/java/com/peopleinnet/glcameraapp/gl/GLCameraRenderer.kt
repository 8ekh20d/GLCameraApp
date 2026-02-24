package com.peopleinnet.glcameraapp.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.peopleinnet.glcameraapp.filters.GLFilter
import com.peopleinnet.glcameraapp.filters.NormalFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCameraRenderer() : GLSurfaceView.Renderer {

    private val TAG = "GLCameraRenderer"
    
    private lateinit var surfaceTexture: SurfaceTexture
    private var oesTextureId = 0
    var onSurfaceTextureReady: ((SurfaceTexture) -> Unit)? = null
    var frameMetricsListener: FrameMetricsListener? = null

    private lateinit var vertexBuffer: FloatBuffer
    private val transformMatrix = FloatArray(16)

    private var positionHandle = 0
    private var texHandle = 0
    private var textureHandle = 0
    private var transformHandle = 0

    private var currentFilter: GLFilter? = null
    private var surfaceWidth = 0
    private var surfaceHeight = 0

    private val vertices = floatArrayOf(
        // X,   Y,   U,  V
        -1f, -1f, 0f, 0f,
        1f, -1f, 1f, 0f,
        -1f, 1f, 0f, 1f,
        1f, 1f, 1f, 1f
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        try {
            oesTextureId = GLHelper.createOESTexture()

            surfaceTexture = SurfaceTexture(oesTextureId)
            surfaceTexture.setDefaultBufferSize(1080, 1920)
            onSurfaceTextureReady?.invoke(surfaceTexture)

            vertexBuffer = ByteBuffer
                .allocateDirect(vertices.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .apply {
                    put(vertices)
                    position(0)
                }

            // Default filter
            currentFilter = NormalFilter()
            currentFilter?.init()

            GLES20.glClearColor(0f, 0f, 0f, 1f)
            GLHelper.checkGLError("glClearColor")
        } catch (e: Exception) {
            Log.e(TAG, "Surface creation failed", e)
            throw RuntimeException("Failed to create GL surface", e)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        try {
            surfaceWidth = width
            surfaceHeight = height
            GLES20.glViewport(0, 0, width, height)
            GLHelper.checkGLError("glViewport")
        } catch (e: Exception) {
            Log.e(TAG, "Surface change failed", e)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        val start = System.nanoTime()
        
        try {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            GLHelper.checkGLError("glClear")

            surfaceTexture.updateTexImage()
            surfaceTexture.getTransformMatrix(transformMatrix)

            val program = currentFilter?.getProgram() ?: return
            GLES20.glUseProgram(program)
            GLHelper.checkGLError("glUseProgram")

            positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
            texHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
            textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
            transformHandle = GLES20.glGetUniformLocation(program, "uTransform")

            bindVertexData()
            applyTransformMatrix()
            bindTexture()

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLHelper.checkGLError("glDrawArrays")

            val end = System.nanoTime()
            val frameTimeMs = (end - start) / 1_000_000f

            frameMetricsListener?.onFrameProcessed(frameTimeMs)
        } catch (e: Exception) {
            Log.e(TAG, "Frame rendering failed", e)
            // Continue rendering on next frame
        }
    }

    private fun bindVertexData() {
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(
            positionHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * 4,
            vertexBuffer
        )
        GLHelper.checkGLError("glVertexAttribPointer position")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLHelper.checkGLError("glEnableVertexAttribArray position")

        vertexBuffer.position(2)
        GLES20.glVertexAttribPointer(
            texHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * 4,
            vertexBuffer
        )
        GLHelper.checkGLError("glVertexAttribPointer texture")
        GLES20.glEnableVertexAttribArray(texHandle)
        GLHelper.checkGLError("glEnableVertexAttribArray texture")
    }

    private fun applyTransformMatrix() {
        GLES20.glUniformMatrix4fv(
            transformHandle,
            1,
            false,
            transformMatrix,
            0
        )
        GLHelper.checkGLError("glUniformMatrix4fv")
    }

    private fun bindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLHelper.checkGLError("glActiveTexture")
        
        GLES20.glBindTexture(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            oesTextureId
        )
        GLHelper.checkGLError("glBindTexture")
        
        GLES20.glUniform1i(textureHandle, 0)
        GLHelper.checkGLError("glUniform1i")
    }

    fun setFilter(filter: GLFilter) {
        try {
            currentFilter?.release()
            currentFilter = filter
            currentFilter?.init()
        } catch (e: Exception) {
            Log.e(TAG, "Filter change failed", e)
            // Fallback to normal filter
            try {
                currentFilter = NormalFilter()
                currentFilter?.init()
            } catch (fallbackError: Exception) {
                Log.e(TAG, "Fallback filter initialization failed", fallbackError)
            }
        }
    }

    fun getSurfaceTexture(): SurfaceTexture = surfaceTexture

    fun release() {
        try {
            currentFilter?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release filter", e)
        }
        
        try {
            if (::surfaceTexture.isInitialized) {
                surfaceTexture.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release surface texture", e)
        }
    }
}

interface FrameMetricsListener {
    fun onFrameProcessed(frameTimeMs: Float)
}