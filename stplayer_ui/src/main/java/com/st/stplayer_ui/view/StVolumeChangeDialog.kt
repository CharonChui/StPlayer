package com.st.stplayer_ui.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import com.st.stplayer.inter.IVolumeChangeUI
import com.st.stplayer_ui.R
import com.st.stplayer_ui.util.DpUtil

class StVolumeChangeDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = R.style.st_volume_change_dialog
) :
    Dialog(context, themeResId), IVolumeChangeUI {
    private val mHandler = Handler()
    private var mVolumeIcon: ImageView? = null
    private var mProgressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.st_view_volume_change)
        mVolumeIcon = findViewById(R.id.common_ui_audio_progressbar_icon)
        mProgressBar = findViewById(R.id.common_ui_audio_progressbar)
        val mWindow = window
        val mIsLandScapeScreen = false
        if (mWindow != null) {
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            if (mIsLandScapeScreen) {
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                mWindow.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            } else {
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                mWindow.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
            }
            mWindow.setLayout(
                if (mIsLandScapeScreen) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val param = mWindow.attributes
            param.gravity = Gravity.TOP
            param.y = if (mIsLandScapeScreen) 0 else DpUtil.dip2px(context, 10)
            mWindow.attributes = param
        }
    }

    private fun handleVolume(currentVolume: Int, maxVolume: Int) {
        mProgressBar!!.max = maxVolume
        mProgressBar!!.progress = currentVolume
        val volumeIconResId: Int
        volumeIconResId = if (currentVolume >= maxVolume * 2 / 3) {
            R.drawable.st_video_view_volume_3
        } else if (currentVolume >= maxVolume * 3) {
            R.drawable.st_video_view_volume_2
        } else if (currentVolume > 0) {
            R.drawable.st_video_view_volume_1
        } else {
            R.drawable.st_video_view_mute
        }
        mVolumeIcon!!.setImageResource(volumeIconResId)
    }

    override fun show(currentVolume: Int, maxVolume: Int) {
        super.show()
        handleVolume(currentVolume, maxVolume)
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postDelayed({ dismiss() }, DISPLAY_DURATION.toLong())
    }

    override fun dismiss() {
        super.dismiss()
        mHandler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val DISPLAY_DURATION = 1000
    }
}
