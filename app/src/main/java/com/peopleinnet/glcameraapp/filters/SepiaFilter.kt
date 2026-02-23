package com.peopleinnet.glcameraapp.filters

import com.peopleinnet.glcameraapp.R

class SepiaFilter : BaseFilter(
    vertexRes = R.raw.vertex_shader,
    fragmentRes = R.raw.fragment_sepia
)