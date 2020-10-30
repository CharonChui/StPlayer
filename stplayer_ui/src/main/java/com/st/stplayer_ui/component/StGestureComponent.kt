package com.st.stplayer_ui.component

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.component.IGestureComponent
import com.st.stplayer.helper.BrightnessHelper
import com.st.stplayer.helper.VolumeHelper
import com.st.stplayer.util.ActivityUtil
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R
import com.st.stplayer_ui.util.DurationUtil

open class StGestureComponent(context: Context) : AbstractComponent(context), IGestureComponent {
    private var mIconView: ImageView? = null
    private var mTextView: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var mNeedSeekPos = NOT_NEED_SEEK_POS
    private var mBrightness = 0f
    private var mVolume = 0f

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.st_component_view_gesture
    }

    override fun onInit(rootView: View, context: Context) {
        mIconView = rootView.findViewById(R.id.st_ui_view_icon_gesture)
        mTextView = rootView.findViewById(R.id.st_ui_view_tv_gesture)
        mProgressBar = rootView.findViewById(R.id.st_ui_view_progress_gesture)
    }

    override fun onDoubleClick() {
        Toast.makeText(context, "double click", Toast.LENGTH_SHORT).show()
    }

    override fun onLongPress() {
        Toast.makeText(context, "long press", Toast.LENGTH_SHORT).show()
    }

    override fun onScale(scale: Float) {
        show()
        mTextView?.visibility = View.VISIBLE
        mTextView?.text = "scale"
        mIconView?.visibility = View.GONE
        mProgressBar?.visibility = View.GONE
    }

    override fun onScaleStop(isCancel: Boolean) {
        hide()
    }

    override fun onHorizontalSlide(dX: Float) {
        mProgressBar?.visibility = View.GONE
        val width = measuredWidth
        val duration = getDuration()!!
        val currentPosition = getCurrentPosition()!!
        var targetPosition = (dX / width * 120000 + currentPosition).toLong()
        if (targetPosition > duration) {
            targetPosition = duration
        }
        if (targetPosition < 0) {
            targetPosition = 0
        }
        mNeedSeekPos = targetPosition
        if (targetPosition > currentPosition) {
            mIconView?.setImageResource(R.drawable.st_video_component_next)
        } else {
            mIconView?.setImageResource(R.drawable.st_video_component_prev)
        }
        mIconView?.visibility = View.VISIBLE
        mTextView?.text = String.format(
            "%s/%s", DurationUtil.stringForTime(targetPosition),
            DurationUtil.stringForTime(duration)
        )
    }

    override fun onVerticalSlide(e1: MotionEvent, dY: Float) {
        val halfScreen = ActivityUtil.getScreenWidth(context) / 2
        val height = measuredHeight
        if (e1.x > halfScreen) {
            val maxVolume = VolumeHelper.getInstance().getSystemMaxVolume(context)
            LogUtil.i(
                LOG_TAG,
                "onVerticalSlide : $dY"
            )
            // 太灵敏，除以2
            val percent = dY / height
            LogUtil.i(
                LOG_TAG,
                "onVerticalSlide percent : $percent"
            )
            val volumeChange =
                percent / DISTANCE_SCALE * maxVolume
            LogUtil.i(
                LOG_TAG,
                "onVerticalSlide volumeChange : $volumeChange"
            )
            var targetVolume = mVolume + volumeChange
            LogUtil.i(
                LOG_TAG,
                "onVerticalSlide targetVolume : $targetVolume..max..$maxVolume"
            )
            if (targetVolume > maxVolume) {
                targetVolume = maxVolume.toFloat()
            }
            if (targetVolume < 0) {
                targetVolume = 0f
            }
            VolumeHelper.getInstance().setSystemVolume(context, targetVolume.toInt())
            onVolumeChange(((targetVolume / maxVolume * 100).toInt()))
        } else {
            val percent = dY / height
            val brightnessChange =
                percent / DISTANCE_SCALE * 1.0f
            var targetBright = mBrightness + brightnessChange
            if (targetBright < 0) {
                targetBright = 0f
            }
            if (targetBright > 1.0f) {
                targetBright = 1.0f
            }
            BrightnessHelper.getInstance()!!.setBrightness(context, targetBright)
            onBrightChange((targetBright * 100).toInt())
        }
    }

    override fun onGestureStart() {
        mBrightness = BrightnessHelper.getInstance()!!.getBrightness(context)
        mVolume = VolumeHelper.getInstance().getSystemVolume(context).toFloat()
        show()
    }

    override fun onGestureStop(isCancel: Boolean) {
        hide()
        if (!isCancel && mNeedSeekPos >= 0) {
            seekTo(mNeedSeekPos)
            mNeedSeekPos = NOT_NEED_SEEK_POS
        }
    }

    override fun onScaleStart() {}
    private fun onBrightChange(percent: Int) {
        mProgressBar?.visibility = View.VISIBLE
        mProgressBar?.progress = percent
        mIconView?.setImageResource(R.drawable.st_video_view_takepic)
        mIconView?.visibility = View.VISIBLE
        mTextView?.text = "$percent%"
    }

    private fun onVolumeChange(percent: Int) {
        mProgressBar?.visibility = View.VISIBLE
        mProgressBar?.progress = percent
        if (percent <= 0) {
            mIconView?.setImageResource(R.drawable.st_video_view_mute)
        } else {
            mIconView?.setImageResource(R.drawable.st_video_view_volume_3)
        }
        mIconView?.visibility = View.VISIBLE
        mTextView?.text = "$percent%"
    }

    companion object {
        private var LOG_TAG = StGestureComponent::class.java.simpleName
        const val NOT_NEED_SEEK_POS = -1L
        private const val DISTANCE_SCALE = 2
    }
}