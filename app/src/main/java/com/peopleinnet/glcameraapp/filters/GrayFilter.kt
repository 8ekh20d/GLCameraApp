package com.peopleinnet.glcameraapp.filters

import android.content.Context
import com.peopleinnet.glcameraapp.R

class GrayFilter() : BaseFilter(
    R.raw.vertex_shader,
    R.raw.fragment_gray
)