package com.st.stplayer.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES30
import androidx.annotation.RawRes
import com.st.stplayer.render.GLTextureView
import com.st.stplayer.renderer.util.ProjectionMatrixUtil
import com.st.stplayer.renderer.util.ResReadUtils
import com.st.stplayer.renderer.util.ShaderUtils
import java.nio.Buffer

abstract class AbsGLTextureViewRenderer : GLTextureView.Renderer {
    protected var mProgram = 0

    /**
     * readResource -> compileShader -> linkProgram -> useProgram
     *
     * @param context
     * @param vertexShader
     * @param fragmentShader
     */
    protected fun handleProgram(
        context: Context,
        @RawRes vertexShader: Int,
        @RawRes fragmentShader: Int
    ) {
        val vertexShaderStr = ResReadUtils.readResource(context, vertexShader)
        val vertexShaderId = ShaderUtils.compileVertexShader(vertexShaderStr!!)
        //编译片段着色程序
        val fragmentShaderStr = ResReadUtils.readResource(context, fragmentShader)
        val fragmentShaderId = ShaderUtils.compileFragmentShader(fragmentShaderStr!!)
        //连接程序
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        //在OpenGLES环境中使用程序
        GLES30.glUseProgram(mProgram)
    }

    protected fun glViewport(x: Int, y: Int, width: Int, height: Int) {
        GLES30.glViewport(x, y, width, height)
    }

    protected fun glClearColor(
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        GLES30.glClearColor(red, green, blue, alpha)
    }

    protected fun glClear(mask: Int) {
        GLES30.glClear(mask)
    }

    protected fun glDisableVertexAttribArray(index: Int) {
        GLES30.glDisableVertexAttribArray(index)
    }

    protected fun glGetAttribLocation(name: String?): Int {
        return GLES30.glGetAttribLocation(mProgram, name)
    }

    protected fun glGetUniformLocation(name: String?): Int {
        return GLES30.glGetUniformLocation(mProgram, name)
    }

    protected fun glUniformMatrix4fv(
        location: Int,
        count: Int,
        transpose: Boolean,
        value: FloatArray?,
        offset: Int
    ) {
        GLES30.glUniformMatrix4fv(location, count, transpose, value, offset)
    }

    protected fun glDrawArrays(mode: Int, first: Int, count: Int) {
        GLES30.glDrawArrays(mode, first, count)
    }

    protected fun glDrawElements(
        mode: Int,
        count: Int,
        type: Int,
        indices: Buffer?
    ) {
        GLES30.glDrawElements(mode, count, type, indices)
    }

    protected fun orthoM(name: String?, width: Int, height: Int) {
        ProjectionMatrixUtil.orthoM(mProgram, width, height, name)
    }

    protected fun glVertexAttribPointer(
        indx: Int,
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        ptr: Buffer?
    ) {
        GLES30.glVertexAttribPointer(indx, size, type, normalized, stride, ptr)
    }

    protected fun glActiveTexture(texture: Int) {
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
    }

    protected fun glBindTexture(target: Int, texture: Int) {
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture)
    }

    protected fun glUniform1i(location: Int, x: Int) {
        GLES20.glUniform1i(location, x)
    }

    companion object {
        protected fun glEnableVertexAttribArray(index: Int) {
            GLES30.glEnableVertexAttribArray(index)
        }
    }
}