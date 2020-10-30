package com.st.stplayer.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import com.st.stplayer.R
import com.st.stplayer.render.GLTextureView
import com.st.stplayer.renderer.util.BufferUtil
import com.st.stplayer.renderer.util.TextureUtil
import com.st.stplayer.util.MainThreadUtil
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLTextureRender(private val mContext: Context, private var mGLTextureView: GLTextureView) :
    AbsGLTextureViewRenderer() {
    private var mTextureId = 0
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mUpdateSurfaceTexture = false
    private val mVertextBuffer: FloatBuffer
    private val mTextureBuffer: FloatBuffer
    private var vertexPosition = 0
    private var texturePosition = 0
    private var samplerTexturePosition = 0

    /**
     * 视频的宽高
     */
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    /**
     * 需改更改渲染的大小
     */
    private var mNeedUpdateSize = false

    /**
     * Surface的宽高
     */
    private var mSurfaceWidth = 0
    private var mSurfaceHeight = 0
    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        glClearColor(0.0f, 1.0f, 0.0f, 1.0f)
        handleProgram(mContext, R.raw.video_vertex_shader, R.raw.video_fragment_shader)
        vertexPosition = glGetAttribLocation("vPosition")
        texturePosition = glGetAttribLocation("vCoordPosition")
        samplerTexturePosition = glGetUniformLocation("uSamplerTexture")
        mTextureId = TextureUtil.createOESTextureId()
        mSurfaceTexture = SurfaceTexture(mTextureId)
        mSurfaceTexture?.let {
            mSurfaceTexture!!.setDefaultBufferSize(
                mGLTextureView.width,
                mGLTextureView.height
            )
            mSurfaceTexture!!.setOnFrameAvailableListener {
                mUpdateSurfaceTexture = true
                mGLTextureView.requestRender()
            }
            MainThreadUtil.post { mTextureRenderListener?.onCreate(mSurfaceTexture!!) }
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        mSurfaceWidth = width
        mSurfaceHeight = height
        adjustVideoSize()
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?): Boolean {
        glClear(GLES30.GL_DEPTH_BUFFER_BIT or GLES30.GL_COLOR_BUFFER_BIT)
        adjustVideoSize()
        glVertexAttribPointer(
            vertexPosition,
            POSITION_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            mVertextBuffer
        )
        glVertexAttribPointer(
            texturePosition,
            TEXTURE_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            mTextureBuffer
        )
        synchronized(this) {
            if (mUpdateSurfaceTexture) {
                mSurfaceTexture!!.updateTexImage()
                mUpdateSurfaceTexture = false
            }
        }
        GLES30.glEnableVertexAttribArray(vertexPosition)
        GLES30.glEnableVertexAttribArray(texturePosition)
        GLES30.glUniform1i(samplerTexturePosition, 0)
        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4)
        GLES30.glFlush()
        GLES30.glDisableVertexAttribArray(vertexPosition)
        GLES30.glDisableVertexAttribArray(texturePosition)
        return true
    }

    override fun onSurfaceDestroyed() {}
    fun setVideoSize(width: Int, height: Int) {
        if (mVideoWidth == width && mVideoHeight == height) {
            return
        }
        // videoWidth 272
        // videoHeight 480
        mVideoWidth = width
        mVideoHeight = height
        mNeedUpdateSize = true
    }

    private fun adjustVideoSize() {
        if (mVideoWidth == 0 || mVideoHeight == 0 || mSurfaceHeight == 0 || mSurfaceWidth == 0) {
            return
        }
        if (!mNeedUpdateSize) {
            return
        }
        mNeedUpdateSize = false
        val widthRation = mSurfaceWidth.toFloat() / mVideoWidth
        val heightRation = mSurfaceHeight.toFloat() / mVideoHeight
        val ration = Math.max(widthRation, heightRation)
        // 把视频宽高最小的一个扩大到Surface的大小
        val targetVideoWidth = Math.round(mVideoWidth * ration)
        val targetVideoHeight = Math.round(mVideoHeight * ration)
        // 扩大之后的宽高除以目前surface的宽高，来算错各自要xy的比例，这俩里面有一个肯定是1
        val rationX = targetVideoWidth.toFloat() / mSurfaceWidth
        val rationY = targetVideoHeight.toFloat() / mSurfaceHeight
        val targetPositionData = floatArrayOf(
            POINT_DATA[0] / rationY,
            POINT_DATA[1] / rationX,
            POINT_DATA[2] / rationY,
            POINT_DATA[3] / rationX,
            POINT_DATA[4] / rationY,
            POINT_DATA[5] / rationX,
            POINT_DATA[6] / rationY,
            POINT_DATA[7] / rationX
        )
        // 换算缩放后的顶点坐标。后面在onDraw()方法中会有这个值设置给顶点着色器
        mVertextBuffer.clear()
        mVertextBuffer.put(targetPositionData)
        mVertextBuffer.position(0)
    }

    private var mTextureRenderListener: IVideoTextureRenderListener? = null
    fun setIVideoTextureRenderListener(render: IVideoTextureRenderListener?) {
        mTextureRenderListener = render
    }

    interface IVideoTextureRenderListener {
        fun onCreate(surfaceTexture: SurfaceTexture)
    }

    companion object {
        /**
         * 坐标占用的向量个数
         */
        private const val POSITION_COMPONENT_COUNT = 2

        // 逆时针顺序排列
        private val POINT_DATA = floatArrayOf(
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
        )

        /**
         * 颜色占用的向量个数
         */
        private const val TEXTURE_COMPONENT_COUNT = 2

        // 纹理坐标(s, t)，t坐标方向和顶点y坐标反着
        private val TEXTURE_DATA = floatArrayOf(
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
        )
    }

    init {
        mVertextBuffer = BufferUtil.getFloatBuffer(POINT_DATA)
        mTextureBuffer = BufferUtil.getFloatBuffer(TEXTURE_DATA)
    }
}