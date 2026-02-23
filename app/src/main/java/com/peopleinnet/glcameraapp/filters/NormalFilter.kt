package com.peopleinnet.glcameraapp.filters

import android.content.Context
import com.peopleinnet.glcameraapp.R

class NormalFilter(context: Context) : BaseFilter(
    context,
    R.raw.vertex_shader,
    R.raw.fragment_oes
)