package com.st.stplayer.render

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import com.st.stplayer.player.IMediaPlayer

class TextureRenderView(context: Context) : TextureView(context),
    IRender, SurfaceTextureListener {
    private var mMeasureHelper: MeasureHelper? = null
    private var mMediaPlayer: IMediaPlayer? = null
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurface: Surface? = null

    init {
        mMeasureHelper = MeasureHelper()
        surfaceTextureListener = this
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
        return bitmap
    }

    override fun release() {
        if (mSurface != null) mSurface!!.release()
        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.release()
            mSurfaceTexture = null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredSize = mMeasureHelper!!.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredSize[0], measuredSize[1])
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        if (mSurfaceTexture != null) {
            surfaceTexture = mSurfaceTexture
        } else {
            mSurfaceTexture = surface
            mSurface = Surface(surface)
            mMediaPlayer?.setSurface(mSurface)
        }
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    companion object {
        private val LOG_TAG = TextureRenderView::class.java.simpleName
    }
}