package com.st.stplayer_ui.component

import android.content.Context
import android.view.View
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R

class StCompletionComponent(context: Context) : AbstractComponent(context) {
    private var mCompletionListener: StCompletionViewListener? = null

    override fun getLayoutId(): Int {
        return R.layout.st_component_view_completion
    }

    override fun onInit(rootView: View, context: Context) {
        setOnClickListener {
            hide()
            if (mCompletionListener != null) {
                mCompletionListener!!.onReplay()
            }
            if (mLogicMediaControl != null) {
                mLogicMediaControl!!.replay(true)
            }
            LogUtil.i(LOG_TAG, "replay")
        }
    }

    fun setStCompletionViewListener(listener: StCompletionViewListener?) {
        mCompletionListener = listener
    }

    override fun onPlayerStateChanged(@PlayerState playerState: Int) {
        LogUtil.i(
            LOG_TAG,
            "st complete view on player state changed : $playerState"
        )
        when (playerState) {
            PlayerState.STATE_COMPLETE -> {
                show()
            }
            else -> hide()
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {}
    interface StCompletionViewListener {
        fun onReplay()
    }

    companion object {
        private val LOG_TAG = StCompletionComponent::class.java.simpleName
    }
}