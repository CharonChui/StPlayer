package com.st.stplayer_ui.component

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R

class StBottomProgressComponent(context: Context) : AbstractComponent(context) {
    private var mProgressBar: ProgressBar? = null

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.st_component_view_bottom_progress
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {}

    override fun onMediaControllerVisibleChange(visibility: Int) {
        super.onMediaControllerVisibleChange(visibility)
        if (View.VISIBLE == visibility) {
            hide()
        } else {
            show()
        }
    }

    override fun onProgressChange(
        currentProgress: Long,
        totalDuration: Long
    ) {
        if (View.VISIBLE == visibility) {
            LogUtil.i(
                LOG_TAG,
                "progress change: $currentProgress..$totalDuration"
            )
            if (currentProgress >= 0 && mProgressBar != null) {
                // use long to avoid overflow
                val pos =
                    MAX_PROGRESS * currentProgress / totalDuration
                mProgressBar?.progress = pos.toInt()
                LogUtil.i(
                    LOG_TAG,
                    "progress : " + pos + "..max.." + mProgressBar!!.max
                )
            }
        }
    }

    override fun onBufferPercentChange(percent: Int) {
        LogUtil.i(
            LOG_TAG,
            "onBufferPercentChange change: $percent"
        )
        if (View.VISIBLE == visibility && mProgressBar != null) {
            mProgressBar?.secondaryProgress = percent * 10
        }
    }

    override fun setDataSource(url: String) {
        super.setDataSource(url)
        mProgressBar?.progress = 0
    }

    override fun onInit(rootView: View, context: Context) {
        mProgressBar =
            rootView.findViewById(R.id.st_ui_view_completion_bottom_progress)
        mProgressBar?.max = MAX_PROGRESS
    }

    companion object {
        private val LOG_TAG = StBottomProgressComponent::class.java.simpleName
        private const val MAX_PROGRESS = 1000
    }
}