package com.st.stplayer.component

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.st.stplayer.inter.IControllerPlayerControl

abstract class AbstractComponent(context: Context) : FrameLayout(context), IComponent {
    protected var mLogicMediaControl: IControllerPlayerControl? = null
    protected var mRootView: View? = null

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    override fun getView(): View {
        return this
    }

    override fun onPlayerStateChanged(playerState: Int) {

    }

    override fun setControllerPlayerControl(playerControl: IControllerPlayerControl) {
        mLogicMediaControl = playerControl
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {

    }

    override fun onLockStateChange(isLock: Boolean) {

    }

    override fun onMediaControllerVisibleChange(visibility: Int) {

    }

    override fun onProgressChange(currentProgress: Long, totalDuration: Long) {

    }

    override fun onBufferPercentChange(percent: Int) {

    }

    override fun getDuration(): Long? {
        return mLogicMediaControl?.getDuration()
    }

    override fun getCurrentPosition(): Long? {
        return mLogicMediaControl?.getCurrentPosition()
    }

    override fun seekTo(pos: Long) {
        mLogicMediaControl?.seekTo(pos)
    }

    override fun setDataSource(url: String) {

    }

    override fun show() {
        if (mRootView == null) {
            mRootView = View.inflate(context, getLayoutId(), this)
            onInit(mRootView!!, context)
        }
        hide()
        bringToFront()
        visibility = View.VISIBLE
    }

    abstract fun onInit(rootView: View, context: Context)

    override fun hide() {
        visibility = View.GONE
    }
}