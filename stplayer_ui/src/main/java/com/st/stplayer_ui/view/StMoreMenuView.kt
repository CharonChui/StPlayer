package com.st.stplayer_ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.st.stplayer.helper.BrightnessHelper
import com.st.stplayer.helper.VolumeHelper
import com.st.stplayer.helper.VolumeHelper.OnVolumeChangeListener
import com.st.stplayer_ui.R
import com.st.stplayer_ui.util.DpUtil

class StMoreMenuView(private val mContext: Context) :
    View.OnClickListener {
    private var mRootView: View? = null
    private var mPopupWindow: PopupWindow? = null
    private var mTakePic: View? = null
    private var mLightSeekBar: SeekBar? = null
    private var mVolumeSeekBar: SeekBar? = null
    private var mBrightnessProgress = 0
    private var mOnOperationListener: OnOperationListener? = null

    fun show(view: View?, isMute: Boolean) {
        if (mRootView == null) {
            mRootView = View.inflate(mContext, R.layout.st_view_more_menu, null)
            findView()
            initView()
        }
        handleVolume(isMute)
        handleLight()
        if (mPopupWindow == null) {
            mPopupWindow = PopupWindow(
                mRootView, DpUtil.dip2px(mContext, 450),
                FrameLayout.LayoutParams.MATCH_PARENT, false
            )
        }
        mPopupWindow?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.isOutsideTouchable = true
            it.isTouchable = true
            it.isClippingEnabled = false
            it.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            it.showAtLocation(view, Gravity.RIGHT, 0, 0)
        }

        VolumeHelper.getInstance()
            .setVolumeChangeListener(mContext, object : OnVolumeChangeListener {
                override fun onVolumeChange(volume: Int) {
                    handleVolume(volume <= 0)
                }
            })
    }

    private fun findView() {
        mTakePic = mRootView!!.findViewById(R.id.st_ui_view_more_menu_takpic)
        mLightSeekBar = mRootView!!.findViewById(R.id.st_ui_more_menu_light_seekbar)
        mVolumeSeekBar = mRootView!!.findViewById(R.id.st_ui_more_menu_volume_seekbar)
    }

    private fun initView() {
        mTakePic!!.setOnClickListener(this)
        mLightSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    BrightnessHelper.getInstance()!!.setBrightness(mContext, progress * 1.0f / 100)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        mVolumeSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    VolumeHelper.getInstance().setSystemVolume(mContext, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun handleVolume(isMute: Boolean) {
        val volumeHelper = VolumeHelper.getInstance()
        mVolumeSeekBar!!.max = volumeHelper.getSystemMaxVolume(mContext)
        if (isMute) {
            mVolumeSeekBar!!.progress = 0
        } else {
            mVolumeSeekBar!!.progress = volumeHelper.getSystemVolume(mContext)
        }
    }

    private fun handleLight() {
        mBrightnessProgress =
            (BrightnessHelper.getInstance()!!.getBrightness(mContext) * 100).toInt()
        mLightSeekBar!!.progress = mBrightnessProgress
    }

    fun hide() {
        if (mPopupWindow != null) {
            if (mPopupWindow!!.isShowing) {
                mPopupWindow!!.dismiss()
            }
        }
        VolumeHelper.getInstance().clearVolumeChangeListener(mContext)
    }

    val isShowing: Boolean
        get() = mPopupWindow != null && mPopupWindow!!.isShowing

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.st_ui_view_more_menu_takpic) {
            if (mOnOperationListener != null) {
                mOnOperationListener!!.onTakeScreenShot()
            }
        }
    }

    fun setOnOperationListener(listener: OnOperationListener?) {
        mOnOperationListener = listener
    }

    interface OnOperationListener {
        fun onTakeScreenShot(): Bitmap?
    }

}