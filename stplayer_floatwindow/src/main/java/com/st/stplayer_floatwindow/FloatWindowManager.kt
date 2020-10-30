package com.st.stplayer_floatwindow

import android.content.Context
import com.st.stplayer.StVideoView
import com.st.stplayer_floatwindow.util.DensityUtil
import com.st.stplayer_floatwindow.util.ViewUtil

class FloatWindowManager private constructor() {
    private var mShowing = false
    private var mFloatWindow: FloatWindow? = null
    private var mVideoView: StVideoView? = null

    private object FloatWindowManagerHolder {
        val preloadManager = FloatWindowManager()
    }

    companion object {
        fun getInstance(): FloatWindowManager {
            return FloatWindowManagerHolder.preloadManager
        }
    }

    fun showFloatWindow(
        context: Context,
        videoView: StVideoView
    ) {
        val width: Float = DensityUtil.convertDpToPixel(250.0f, context)
        val height = width * 9 / 16
        showFloatWindow(context, videoView, 0, 0, width.toInt(), height.toInt())
    }


    fun showFloatWindow(
        context: Context, videoView: StVideoView,
        xPos: Int, yPos: Int, width: Int, height: Int
    ) {
        if (mFloatWindow == null) {
            mFloatWindow = FloatWindow(context, xPos, yPos, width, height)
        }
        ViewUtil.removeViewFormParent(videoView)
        mVideoView = videoView
        mFloatWindow!!.addView(videoView)
        val success = mFloatWindow!!.showWindow()
        if (success) {
            mShowing = true
        }
    }

    fun hideFloatWindow() {
        if (mFloatWindow == null) {
            return
        }
        val success = mFloatWindow!!.hideWindow()
        ViewUtil.removeViewFormParent(mVideoView)
        if (success) {
            mShowing = false
        }
    }

    fun isFloatWindowShowing(): Boolean {
        return mShowing
    }

}