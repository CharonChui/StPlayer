package com.st.stplayer.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.view.Surface
import android.view.View
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.renderer.GLSurfaceRender
import com.st.stplayer.util.LogUtil

class GLSurfaceRenderView(context: Context) : GLSurfaceView(context),
    IRender {
    private var mMeasureHelper: MeasureHelper? = null
    private var mMediaPlayer: IMediaPlayer? = null
    private var mGLSurfaceRender: GLSurfaceRender


    init {
        mMeasureHelper = MeasureHelper()
        setEGLContextClientVersion(3)
        mGLSurfaceRender = GLSurfaceRender(getContext(), this)
        mGLSurfaceRender.setIVideoTextureRenderListener(object :
            GLSurfaceRender.IVideoTextureRenderListener {
            override fun onCreate(surfaceTexture: SurfaceTexture) {
                LogUtil.i(
                    LOG_TAG,
                    "surface on create $surfaceTexture"
                )
                if (mMediaPlayer != null) {
                    val surface = Surface(surfaceTexture)
                    mMediaPlayer!!.setSurface(surface)
                }
            }
        })
        setRenderer(mGLSurfaceRender)
    }

    override fun attachToPlayer(player: IMediaPlayer) {
        mMediaPlayer = player
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper!!.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
    }

    override fun setVideoRotation(degree: Int) {
        mMeasureHelper!!.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setScaleType(scaleType: Int) {
        mMeasureHelper!!.setScaleType(scaleType)
        requestLayout()
    }

    override fun getView(): View {
        return this
    }

    override fun takeScreenShot(): Bitmap? {
        return null
    }

    override fun release() {}
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredSize = mMeasureHelper!!.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredSize[0], measuredSize[1])
    }

    companion object {
        private val LOG_TAG = GLSurfaceRenderView::class.java.simpleName
    }
}