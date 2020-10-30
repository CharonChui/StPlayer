package com.st.stplayer.render

import android.content.Context
import com.st.stplayer.type.RenderType

interface IRenderFactory {
    fun getRenderView(@RenderType renderType: Int, context: Context): IRender
}