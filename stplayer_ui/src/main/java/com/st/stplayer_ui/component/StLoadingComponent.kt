package com.st.stplayer_ui.component

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R
import com.st.stplayer_ui.view.StCustomLoadingView

class StLoadingComponent(context: Context) : AbstractComponent(context) {
    private var mStCustomLoadingView: StCustomLoadingView? = null

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.st_component_view_loading
    }

    override fun onInit(rootView: View, context: Context) {
        mStCustomLoadingView = rootView.findViewById(R.id.st_ui_view_loading)
    }

    override fun onPlayerStateChanged(playerState: Int) {
        LogUtil.i(
            LOG_TAG,
            "st prepare view on player state changed : $playerState"
        )
        when (playerState) {
            PlayerState.STATE_BUFFERING -> {
                show()
            }
            else -> hide()
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {}

    companion object {
        private val LOG_TAG = StLoadingComponent::class.java.simpleName
    }
}