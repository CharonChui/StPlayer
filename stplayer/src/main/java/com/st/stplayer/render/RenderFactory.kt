package com.st.stplayer.render

import android.content.Context
import com.st.stplayer.type.RenderType

class RenderFactory : IRenderFactory {
    override fun getRenderView(renderType: Int, context: Context): IRender {
        return when (renderType) {
            RenderType.RENDER_TYPE_SURFACE -> SurfaceRenderView(context)
            RenderType.RENDER_TYPE_GL_SURFACE -> GLSurfaceRenderView(context)
            RenderType.RENDER_TYPE_GL_TEXTURE -> GLTextureRenderView(context)
            RenderType.RENDER_TYPE_AUTO, RenderType.RENDER_TYPE_TEXTURE -> TextureRenderView(
                context
            )
            else -> TextureRenderView(context)
        }
    }
}