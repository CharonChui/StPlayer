package com.st.stplayer.mediacontroller

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.st.stplayer.component.IComponent
import com.st.stplayer.inter.IControllerPlayerControl
import com.st.stplayer.inter.IMediaPlayerControl
import com.st.stplayer.logiccontroller.ILogicController

abstract class AbstractMediaController(context: Context) : FrameLayout(context), IMediaController,
    IControllerPlayerControl {

    protected var mPlayer: IMediaPlayerControl? = null
    protected var mAnchor: View? = null
    protected var mRoot: View? = null
    protected var mLogicControllerList: MutableList<ILogicController>? = null
    protected var mComponentList: MutableList<IComponent>? = null
    protected var mShowTimeoutMs = DEFAULT_SHOW_TIMEOUT_MS

    private val mFadeOut = Runnable { hide() }

    override fun setMediaPlayer(player: IMediaPlayerControl) {
        mPlayer = player
        updatePausePlay()
    }

    open fun updatePausePlay() {

    }

    override fun setAnchorView(view: View) {
        mAnchor = view
        val frameParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        removeAllViews()
        val rootView: View? = makeControllerView()
        rootView?.let {
            addView(rootView, frameParams)
            onInitLogicController()
            initControllerView(it)
            onInitComponent()
        }
    }

    private fun makeControllerView(): View? {
        val inflate =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mRoot = inflate.inflate(getLayoutId(), null, false)
        mRoot?.visibility = View.GONE
        return mRoot
    }

    protected open fun onInitLogicController() {

    }

    protected open fun initControllerView(view: View) {
        if (mLogicControllerList != null) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onInitControllerView(view)
            }
        }
    }

    private fun handleVisibility(visibility: Int) {
        if (mRoot != null) {
            mRoot!!.visibility = visibility
        }

        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                Log.e("@@@", "logic controller : visible change : " + visibility)
                logicControl.onMediaControllerVisibleChange(visibility)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onMediaControllerVisibleChange(visibility)
            }
        }

        mVisibilityListener?.onVisibilityChange(visibility)
    }

    protected open fun onInitComponent() {

    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun isShowing(): Boolean {
        if (mRoot == null) {
            return false
        }
        return View.VISIBLE == mRoot!!.visibility
    }

    override fun getView(): View {
        return this
    }

    override fun replay(resetCurrentPosition: Boolean) {
        mPlayer?.replay(resetCurrentPosition)
    }

    override fun start() {
        mPlayer?.start()
    }

    override fun pause() {
        mPlayer?.pause()
    }

    override fun addLogicControl(logicController: ILogicController) {
        val view = logicController.getView()
        view?.let {
            if (mLogicControllerList == null) {
                mLogicControllerList = ArrayList()
            }
            mLogicControllerList?.let {
                if (!mLogicControllerList!!.contains(logicController)) {
                    mLogicControllerList!!.add(logicController)
                    addView(view)
                    logicController.setControllerPlayerControl(this)
                }
            }
        }
    }

    override fun removeLogicControl(logicController: ILogicController) {
        mLogicControllerList?.remove(logicController)
    }

    override fun clearLogicControl() {
        mLogicControllerList?.clear()
        mLogicControllerList = null
    }

    override fun addComponent(component: IComponent) {
        val view = component.getView()
        if (mComponentList == null) {
            mComponentList = ArrayList()
        }
        mComponentList?.let {
            if (!it.contains(component)) {
                it.add(component)
                addView(view)
                component.setControllerPlayerControl(this)
            }
        }
    }

    override fun removeComponent(component: IComponent) {
        val view = component.getView()
        removeView(view)
        mComponentList?.let {
            if (it.contains(component)) {
                it.remove(component)
            }
        }
    }

    override fun clearComponent() {
        mComponentList?.clear()
        mComponentList = null
    }

    override fun onPlayerStateChange(playerState: Int) {
        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onPlayerStateChange(playerState)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onPlayerStateChanged(playerState)
            }
        }
    }

    override fun onLockStateChange(isLock: Boolean) {
        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onLockStateChange(isLock)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onLockStateChange(isLock)
            }
        }
    }

    override fun onProgressChange(currentProgress: Long, totalDuration: Long) {
        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onProgressChange(currentProgress, totalDuration)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onProgressChange(currentProgress, totalDuration)
            }
        }
    }

    override fun onBufferPercentChange(percent: Int) {
        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onBufferPercentChange(percent)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onBufferPercentChange(percent)
            }
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {
        if (mLogicControllerList != null && mLogicControllerList!!.isNotEmpty()) {
            for (logicControl in mLogicControllerList!!) {
                logicControl.onFullScreenStateChange(fullScreen)
            }
        }

        if (mComponentList != null && mComponentList!!.isNotEmpty()) {
            for (component in mComponentList!!) {
                component.onFullScreenStateChange(fullScreen)
            }
        }
    }

    override fun getDuration(): Long {
        return if (mPlayer == null) {
            0
        } else {
            mPlayer!!.getDuration()
        }
    }

    override fun getCurrentPosition(): Long {
        mPlayer?.let {
            return mPlayer!!.getCurrentPosition()
        }
        return 0
    }

    override fun seekTo(pos: Long) {
        mPlayer?.seekTo(pos)
    }

    override fun release() {
        mLogicControllerList?.clear()
        mLogicControllerList = null
        mComponentList?.clear()
        mComponentList = null
    }

    override fun setDataSource(url: String) {
        mLogicControllerList?.let {
            if (it.isNotEmpty()) {
                for (logicControl in it) {
                    logicControl.setDataSource(url)
                }
            }
        }
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    component.setDataSource(url)
                }
            }
        }
    }

    override fun hide() {
        mRoot?.visibility = View.GONE
        handleVisibility(View.GONE)
    }

    override fun show() {
        show(mShowTimeoutMs)
    }

    open fun show(timeout: Int) {
        mRoot?.visibility = View.VISIBLE
        handleVisibility(View.VISIBLE)
        if (timeout != 0) {
            removeCallbacks(mFadeOut)
            postDelayed(mFadeOut, timeout.toLong())
        }
    }

    override fun hideMediaController() {
        hide()
    }

    private var mVisibilityListener: VisibilityListener? = null

    open fun setVisibilityListener(listener: VisibilityListener?) {
        mVisibilityListener = listener
    }

    /**
     * Listener to be notified about changes of the visibility of the UI control.
     */
    interface VisibilityListener {
        /**
         * Called when the visibility changes.
         *
         * @param visibility The new visibility. Either [View.VISIBLE] or [View.GONE].
         */
        fun onVisibilityChange(visibility: Int)
    }

    companion object {
        const val DEFAULT_SHOW_TIMEOUT_MS = 5000
    }
}
