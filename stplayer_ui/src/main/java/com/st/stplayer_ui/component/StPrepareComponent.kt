package com.st.stplayer_ui.component

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.LayoutRes
import com.st.stplayer.component.AbstractComponent
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.R

class StPrepareComponent(context: Context) : AbstractComponent(context) {
    private var mLoadingView: StLoadingComponent? = null
    var coverImageView: ImageView? = null

    init {
        mRootView = View.inflate(context, getLayoutId(), this)
        onInit(mRootView!!, context)
    }

    @LayoutRes
    override fun getLayoutId(): Int {
        return R.layout.st_component_view_prepare
    }

    override fun onInit(rootView: View, context: Context) {
        mLoadingView = rootView.findViewById(R.id.st_ui_view_loading_prepare)
        coverImageView = rootView.findViewById(R.id.st_ui_view_prepare_cover)
    }

    override fun onPlayerStateChanged(playerState: Int) {
        LogUtil.i(
            LOG_TAG,
            "st prepare view on player state changed : $playerState"
        )
        when (playerState) {
            PlayerState.STATE_PREPARING -> {
                show()
                LogUtil.i(
                    LOG_TAG,
                    "st prepare view show"
                )
            }
            else -> {
                hide()
                LogUtil.i(
                    LOG_TAG,
                    "st prepare view hide"
                )
            }
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {}

    companion object {
        private val LOG_TAG = StPrepareComponent::class.java.simpleName
    }
}