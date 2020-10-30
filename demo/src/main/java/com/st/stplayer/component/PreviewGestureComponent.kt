package com.st.stplayer.component

import android.content.Context
import android.view.MotionEvent
import android.view.View
import com.st.stplayer.R
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.component.StGestureComponent
import kotlinx.android.synthetic.main.component_preview_gesture.view.*

class PreviewGestureComponent(context: Context) : StGestureComponent(context) {
    private var mUrl: String? = null
    private var mLastPreviewPos: Long = -1


    override fun getLayoutId(): Int {
        return R.layout.component_preview_gesture
    }

    override fun onHorizontalSlide(dX: Float) {
        super.onHorizontalSlide(dX)
        mPreviewRoot.visibility = View.VISIBLE
        mVerticalRoot.visibility = View.GONE
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
        LogUtil.i(
            LOG_TAG,
            "last preview pos : $mLastPreviewPos...target...$targetPosition"
        )
        if (mPreview != null && Math.abs(mLastPreviewPos - targetPosition) > 1000) {
            mLastPreviewPos = targetPosition
            LogUtil.i(
                LOG_TAG,
                "show preview and target pos is : $targetPosition"
            )
            mPreview.showPreView(mUrl, targetPosition)
        }
    }

    override fun onVerticalSlide(e1: MotionEvent, dY: Float) {
        super.onVerticalSlide(e1, dY)
        mPreviewRoot.visibility = View.GONE
        mVerticalRoot.visibility = View.VISIBLE
        mLastPreviewPos = -1
    }

    override fun hide() {
        super.hide()
        mLastPreviewPos = -1
    }

    fun setUrl(url: String?) {
        mUrl = url
    }

    companion object {
        private val LOG_TAG = PreviewGestureComponent::class.java.simpleName
    }
}