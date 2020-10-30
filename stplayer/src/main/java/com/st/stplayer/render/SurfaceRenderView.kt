package com.st.stplayer.render

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.util.LogUtil

class SurfaceRenderView : SurfaceView, IRender, SurfaceHolder.Callback {
    private lateinit var mMeasureHelper: MeasureHelper
    private var mMediaPlayer: IMediaPlayer? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        mMeasureHelper = MeasureHelper()
        holder.addCallback(this)
    }

    override fun attachToPlayer(player: IMediaPlayer) {
        mMediaPlayer = player
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight)
            requestLayout()
        }
    }

    override fun setVideoRotation(degree: Int) {
        mMeasureHelper.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setScaleType(scaleType: Int) {
        mMeasureHelper.setScaleType(scaleType)
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
        val measuredSize: IntArray = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredSize[0], measuredSize[1])
    }

    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceChanged(
        holder: SurfaceHolder,
        format: Int,
        width: Int,
        height: Int
    ) {
        LogUtil.i(
            LOG_TAG,
            "surface changed and mediaplayer set surface"
        )
        if (holder == null) {
            mMediaPlayer?.setSurface(null)
        } else {
            mMediaPlayer?.setSurface(holder.surface)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {}

    companion object {
        private val LOG_TAG = SurfaceRenderView::class.java.simpleName
    }
}