package com.st.stplayer.type

import androidx.annotation.IntDef

/**
 * 渲染类型
 * 修改时需同步修改values/attrs.xml
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    RenderType.RENDER_TYPE_AUTO,
    RenderType.RENDER_TYPE_SURFACE,
    RenderType.RENDER_TYPE_TEXTURE,
    RenderType.RENDER_TYPE_GL_SURFACE,
    RenderType.RENDER_TYPE_GL_TEXTURE
)
annotation class RenderType {
    companion object {
        const val RENDER_TYPE_AUTO = 0
        const val RENDER_TYPE_SURFACE = 1
        const val RENDER_TYPE_TEXTURE = 2
        const val RENDER_TYPE_GL_SURFACE = 3
        const val RENDER_TYPE_GL_TEXTURE = 4
    }
}