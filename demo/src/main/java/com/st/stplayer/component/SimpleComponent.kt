package com.st.stplayer.component

import android.content.Context
import android.view.View
import com.st.stplayer.R
import com.st.stplayer.state.PlayerState

class SimpleComponent(context: Context) : AbstractComponent(context) {
    override fun getLayoutId(): Int {
        return R.layout.component_simple
    }

    override fun onInit(rootView: View, context: Context) {
        setOnClickListener {
            mLogicMediaControl?.replay(true)
        }
    }

    override fun onPlayerStateChanged(playerState: Int) {
        super.onPlayerStateChanged(playerState)
        if (PlayerState.STATE_COMPLETE == playerState) {
            show()
        } else {
            hide()
        }
    }
}