package com.peopleinnet.glcameraapp.filters

import android.content.Context
import android.opengl.GLES20
import com.peopleinnet.glcameraapp.gl.ShaderLoader

abstract class BaseFilter(
    private val context: Context,
    private val vertexRes: Int,
    private val fragmentRes: Int
) : GLFilter {

    protected var glProgram = 0

    override fun init(context: Context) {
        val vertex = ShaderLoader.loadRaw(context, vertexRes)
        val fragment = ShaderLoader.loadRaw(context, fragmentRes)
        glProgram = createProgram(vertex, fragment)
    }

    override fun use() {
        GLES20.glUseProgram(glProgram)
    }

    override fun getProgram(): Int = glProgram

    override fun release() {
        GLES20.glDeleteProgram(glProgram)
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
}