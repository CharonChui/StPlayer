package com.st.stplayer.renderer.util

import android.opengl.GLES30
import android.opengl.Matrix

class ProjectionMatrixUtil {
    companion object {
        // 矩阵数组
        private val mProjectionMatrix =
            floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)

        fun orthoM(program: Int, width: Int, height: Int, name: String?) {
            val uMatrixLocation = GLES30.glGetUniformLocation(program, name)
            //计算宽高比 边长比(>=1)，非宽高比
            val aspectRatio =
                if (width > height) width.toFloat() / height.toFloat() else height.toFloat() / width.toFloat()
            if (width > height) {
                // 横屏
                Matrix.orthoM(
                    mProjectionMatrix,
                    0,
                    -aspectRatio,
                    aspectRatio,
                    -1f,
                    1f,
                    -1f,
                    1f
                )
            } else {
                // 竖屏or正方形
                Matrix.orthoM(
                    mProjectionMatrix,
                    0,
                    -1f,
                    1f,
                    -aspectRatio,
                    aspectRatio,
                    -1f,
                    1f
                )
            }
            GLES30.glUniformMatrix4fv(
                uMatrixLocation,
                1,
                false,
                mProjectionMatrix,
                0
            )
        }
    }
}