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

class GLCameraRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private lateinit var surfaceTexture: SurfaceTexture
    private var oesTextureId = 0
    var onSurfaceTextureReady: ((SurfaceTexture) -> Unit)? = null

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
        currentFilter = NormalFilter(context)
        currentFilter?.init(context)

        GLES20.glClearColor(0f, 0f, 0f, 1f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        surfaceTexture.updateTexImage()
        surfaceTexture.getTransformMatrix(transformMatrix)

        val program = currentFilter?.getProgram() ?: return
        GLES20.glUseProgram(program)

        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        transformHandle = GLES20.glGetUniformLocation(program, "uTransform")

        bindVertexData()
        applyTransformMatrix()
        bindTexture()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
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
        GLES20.glEnableVertexAttribArray(positionHandle)

        vertexBuffer.position(2)
        GLES20.glVertexAttribPointer(
            texHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * 4,
            vertexBuffer
        )
        GLES20.glEnableVertexAttribArray(texHandle)
    }

    private fun applyTransformMatrix() {
        GLES20.glUniformMatrix4fv(
            transformHandle,
            1,
            false,
            transformMatrix,
            0
        )
    }

    private fun bindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            oesTextureId
        )
        GLES20.glUniform1i(textureHandle, 0)
    }

    fun setFilter(filter: GLFilter) {
        Log.e("GrayTest", "renderer click start")
        currentFilter?.release()
        currentFilter = filter
        currentFilter?.init(context)
        Log.e("GrayTest", "renderer click end")

    }

    fun getSurfaceTexture(): SurfaceTexture = surfaceTexture

    fun release() {
        currentFilter?.release()
        surfaceTexture.release()
    }
}