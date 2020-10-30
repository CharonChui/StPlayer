package com.st.stplayer.logiccontroller

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.st.stplayer.inter.IControllerPlayerControl


abstract class AbstractLogicController(context: Context) : FrameLayout(context), ILogicController {
    protected var mControllerPlayerControl: IControllerPlayerControl? = null
    protected var mRoot: View? = null

    override fun setControllerPlayerControl(controllerPlayerControl: IControllerPlayerControl) {
        mControllerPlayerControl = controllerPlayerControl
    }

    override fun onProgressChange(currentProgress: Long, totalDuration: Long) {
    }

    override fun onPlayerStateChange(playerState: Int) {
    }

    override fun onInitControllerView(mRoot: View) {
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {
    }

    override fun onLockStateChange(isLock: Boolean) {
    }

    override fun onBufferPercentChange(percent: Int) {
    }

    override fun onMediaControllerVisibleChange(visibility: Int) {
    }

    override fun setDataSource(url: String) {
    }


    override fun show() {
        if (mRoot == null) {
            mRoot = View.inflate(context, getLayoutId(), this)
            onInit(context)
        }
        bringToFront()
        visibility = View.VISIBLE
    }

    abstract fun onInit(context: Context)

    abstract fun getLayoutId(): Int

    override fun hide() {
        visibility = View.GONE
    }

    override fun isShowing(): Boolean {
        return visibility == View.VISIBLE
    }

    override fun getView(): View? {
        return this
    }
}