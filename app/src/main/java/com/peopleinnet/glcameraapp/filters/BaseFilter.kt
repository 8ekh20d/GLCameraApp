package com.peopleinnet.glcameraapp.filters

import android.content.Context
import android.opengl.GLES20
import com.peopleinnet.glcameraapp.gl.GLHelper
import com.peopleinnet.glcameraapp.gl.ShaderLoader

abstract class BaseFilter(
    private val vertexRes: Int,
    private val fragmentRes: Int
) : GLFilter {

    protected var glProgram = 0
    private val TAG = "BaseFilter"

    override fun init() {
        try {
            val vertex = ShaderLoader.loadRaw(vertexRes)
            val fragment = ShaderLoader.loadRaw(fragmentRes)
            glProgram = createProgram(vertex, fragment)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Filter initialization failed", e)
            throw RuntimeException("Failed to initialize filter", e)
        }
    }

    override fun use() {
        GLES20.glUseProgram(glProgram)
        GLHelper.checkGLError("glUseProgram")
    }

    override fun getProgram(): Int = glProgram

    override fun release() {
        if (glProgram != 0) {
            GLES20.glDeleteProgram(glProgram)
            GLHelper.checkGLError("glDeleteProgram")
            glProgram = 0
        }
    }

    private fun createProgram(vertex: String, fragment: String): Int {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertex)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragment)

        val program = GLES20.glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Failed to create GL program")
        }

        GLES20.glAttachShader(program, vertexShader)
        GLHelper.checkGLError("glAttachShader vertex")
        
        GLES20.glAttachShader(program, fragmentShader)
        GLHelper.checkGLError("glAttachShader fragment")
        
        GLES20.glLinkProgram(program)
        GLHelper.checkGLError("glLinkProgram")

        // Check link status
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            val log = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException("Failed to link program: $log")
        }

        // Clean up shaders after linking
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)

        return program
    }

    private fun loadShader(type: Int, code: String): Int {
        val shader = GLES20.glCreateShader(type)
        if (shader == 0) {
            throw RuntimeException("Failed to create shader of type: $type")
        }

        GLES20.glShaderSource(shader, code)
        GLHelper.checkGLError("glShaderSource")
        
        GLES20.glCompileShader(shader)
        GLHelper.checkGLError("glCompileShader")

        // Check compilation status
        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] != GLES20.GL_TRUE) {
            val log = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            val shaderType = if (type == GLES20.GL_VERTEX_SHADER) "vertex" else "fragment"
            throw RuntimeException("Failed to compile $shaderType shader: $log")
        }

        return shader
    }
}