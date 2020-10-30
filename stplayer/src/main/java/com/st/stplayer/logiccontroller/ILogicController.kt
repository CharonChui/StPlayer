package com.st.stplayer.logiccontroller

import android.view.View
import com.st.stplayer.inter.IControllerPlayerControl
import com.st.stplayer.state.PlayerState

/**
 * LogicController接口
 */
interface ILogicController {
    fun setControllerPlayerControl(controllerPlayerControl: IControllerPlayerControl)

    fun onProgressChange(currentProgress: Long, totalDuration: Long)

    fun onPlayerStateChange(@PlayerState playerState: Int)

    fun onInitControllerView(mRoot: View)

    fun onFullScreenStateChange(fullScreen: Boolean)

    fun onLockStateChange(isLock: Boolean)

    fun onBufferPercentChange(percent: Int)

    fun onMediaControllerVisibleChange(visibility: Int)

    fun setDataSource(url: String)

    fun show()

    fun hide()

    fun isShowing(): Boolean

    fun getView(): View?
}