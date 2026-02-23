package com.peopleinnet.glcameraapp.filters

import android.content.Context

interface GLFilter {
    fun init(context: Context)
    fun use()
    fun getProgram(): Int
    fun release()
}