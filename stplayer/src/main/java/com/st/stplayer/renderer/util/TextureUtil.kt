package com.st.stplayer.renderer.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLUtils
import com.st.stplayer.util.LogUtil

class TextureUtil {
    companion object {
        private const val TAG = "TextureHelper"

        fun createOESTextureId(): Int {
            val textures = IntArray(1)
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
                    .toFloat()
            )
            GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
                    .toFloat()
            )
            GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )
            return textures[0]
        }

        /**
         * 根据资源ID获取相应的OpenGL纹理ID，若加载失败则返回0
         * <br></br>必须在GL线程中调用
         */
        fun loadTexture(context: Context, resourceId: Int): TextureBean? {
            val bean = TextureBean()
            val textureObjectIds = IntArray(1)
            // 1. 创建纹理对象
            GLES20.glGenTextures(1, textureObjectIds, 0)
            if (textureObjectIds[0] == 0) {
                LogUtil.w(
                    TAG,
                    "Could not generate a new OpenGL texture object."
                )
                return bean
            }
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(
                context.resources, resourceId, options
            )
            if (bitmap == null) {
                LogUtil.w(
                    TAG,
                    "Resource ID $resourceId could not be decoded."
                )
                // 加载Bitmap资源失败，删除纹理Id
                GLES20.glDeleteTextures(1, textureObjectIds, 0)
                return bean
            }
            // 2. 将纹理绑定到OpenGL对象上
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

            // 3. 设置纹理过滤参数:解决纹理缩放过程中的锯齿问题。若不设置，则会导致纹理为黑色
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR_MIPMAP_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            // 4. 通过OpenGL对象读取Bitmap数据，并且绑定到纹理对象上，之后就可以回收Bitmap对象
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            // Note: Following code may cause an error to be reported in the
            // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
            // Failed to generate texture mipmap levels (error=3)
            // No OpenGL error will be encountered (glGetError() will return
            // 0). If this happens, just squash the source image to be
            // img2. It will look the same because of texture coordinates,
            // and mipmap generation will work.
            // 5. 生成Mip位图
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

            // 6. 回收Bitmap对象
            bean.width = bitmap.width
            bean.height = bitmap.height
            bitmap.recycle()

            // 7. 将纹理从OpenGL对象上解绑，现在OpenGL已经完成了纹理的加载，不需要再绑定此纹理了，后面使用此纹理时通过纹理对象的ID即可
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

            // 所以整个流程中，OpenGL对象类似一个容器或者中间者的方式，将Bitmap数据转移到OpenGL纹理上
            bean.textureId = textureObjectIds[0]
            return bean
        }

        /**
         * 纹理数据
         */
        class TextureBean {
            var textureId = 0
            var width = 0
            var height = 0

        }
    }
}