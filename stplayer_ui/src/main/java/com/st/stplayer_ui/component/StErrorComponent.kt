package com.st.stplayer_ui.component

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R

class StErrorComponent(context: Context) : AbstractComponent(context) {
    var errorMsgTxt: TextView? = null
    var retryTxt: TextView? = null
    private var mErrorListener: StErrorViewListener? = null

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.st_component_view_error
    }

    override fun onInit(rootView: View, context: Context) {
        errorMsgTxt = rootView.findViewById(R.id.st_ui_view_error_msg)
        retryTxt = rootView.findViewById(R.id.st_ui_view_error_retry)
        retryTxt?.setOnClickListener {
            LogUtil.i(LOG_TAG, "error view retry")
            hide()
            mErrorListener?.onRetry()
            mLogicMediaControl?.replay(false)
        }
    }

    fun setStErrorViewListener(listener: StErrorViewListener?) {
        mErrorListener = listener
    }

    override fun onPlayerStateChanged(playerState: Int) {
        LogUtil.i(
            LOG_TAG,
            "st error view on player state changed : $playerState"
        )
        when (playerState) {
            PlayerState.STATE_ERROR -> {
                LogUtil.i(
                    LOG_TAG,
                    "error view show"
                )
                show()
            }
            else -> {
                LogUtil.i(
                    LOG_TAG,
                    "error view hide"
                )
                hide()
            }
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {}
    interface StErrorViewListener {
        fun onRetry()
    }

    companion object {
        private val LOG_TAG = StErrorComponent::class.java.simpleName
    }
}