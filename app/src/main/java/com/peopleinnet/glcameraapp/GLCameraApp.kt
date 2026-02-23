package com.peopleinnet.glcameraapp

import android.app.Application

class GLCameraApp: Application() {

    companion object {
        lateinit var instance: GLCameraApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}