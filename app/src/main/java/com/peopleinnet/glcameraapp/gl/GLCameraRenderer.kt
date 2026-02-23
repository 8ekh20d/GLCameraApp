package com.peopleinnet.glcameraapp.gl

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLCameraRenderer : GLSurfaceView.Renderer {

    private lateinit var surfaceTexture: SurfaceTexture
    private var oesTextureId = 0
    var onSurfaceTextureReady: ((SurfaceTexture) -> Unit)? = null

    private var program = 0
    private lateinit var vertexBuffer: FloatBuffer
    private val transformMatrix = FloatArray(16)

    private var positionHandle = 0
    private var texHandle = 0
    private var textureHandle = 0
    private var transformHandle = 0

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

        program = createProgram(vertexShaderCode, fragmentShaderCode)

        vertexBuffer = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertices)
                position(0)
            }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        positionHandle = GLES20.glGetAttribLocation(program, "aPosition")
        texHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        textureHandle = GLES20.glGetUniformLocation(program, "uTexture")
        transformHandle = GLES20.glGetUniformLocation(program, "uTransform")
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        surfaceTexture.updateTexImage()
        surfaceTexture.getTransformMatrix(transformMatrix)

        GLES20.glUseProgram(program)

        bindVertexData()
        applyTransformMatrix()
        bindTexture()

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun bindVertexData() {
        vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 4 * 4, vertexBuffer)
        GLES20.glEnableVertexAttribArray(positionHandle)

        vertexBuffer.position(2)
        GLES20.glVertexAttribPointer(texHandle, 2, GLES20.GL_FLOAT, false, 4 * 4, vertexBuffer)
        GLES20.glEnableVertexAttribArray(texHandle)
    }

    private fun applyTransformMatrix() {
        GLES20.glUniformMatrix4fv(transformHandle, 1, false, transformMatrix, 0)
    }

    private fun bindTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureId)
        GLES20.glUniform1i(textureHandle, 0)
    }

    private fun createProgram(vertex: String, fragment: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertex)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment)

        return GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun loadShader(type: Int, code: String): Int {
        return GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, code)
            GLES20.glCompileShader(it)
        }
    }

    private val vertexShaderCode = """
        attribute vec4 aPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        uniform mat4 uTransform;

        void main() {
            gl_Position = aPosition;
            vTexCoord = (uTransform * vec4(aTexCoord, 0.0, 1.0)).xy;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;

        varying vec2 vTexCoord;
        uniform samplerExternalOES uTexture;

        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
//            vec4 color = texture2D(uTexture, vTexCoord);
//            float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));
//            gl_FragColor = vec4(vec3(gray), 1.0);
        }
    """.trimIndent()
}