package com.st.stplayer.mediacontroller

import android.content.Context
import android.view.View
import com.st.stplayer.R
import com.st.stplayer.component.SimpleComponent
import com.st.stplayer.logiccontroller.SimpleLogicController
import kotlinx.android.synthetic.main.media_controller_simple.view.*

class SimpleMediaController(context: Context) : AbstractMediaController(context) {
    override fun getLayoutId(): Int {
        return R.layout.media_controller_simple
    }

    override fun onInitLogicController() {
        super.onInitLogicController()
        addLogicControl(SimpleLogicController(context))
    }

    override fun onInitComponent() {
        super.onInitComponent()
        addComponent(SimpleComponent(context))
    }

    override fun initControllerView(view: View) {
        super.initControllerView(view)
        mFullScreen.setOnClickListener {
            mPlayer?.let {
                if (mPlayer!!.isFullScreen()) {
                    mPlayer!!.closeFullScreen()
                } else {
                    mPlayer!!.openFullScreen()
                }
            }
        }
    }
}