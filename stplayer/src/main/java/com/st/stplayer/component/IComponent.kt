package com.st.stplayer.component

import android.view.View
import com.st.stplayer.inter.IControllerPlayerControl
import com.st.stplayer.state.PlayerState

/**
 * Component接口
 */
interface IComponent {
    fun getView(): View

    fun show()

    fun hide()

    fun onPlayerStateChanged(@PlayerState playerState: Int)

    fun setControllerPlayerControl(playerControl: IControllerPlayerControl)

    fun onFullScreenStateChange(fullScreen: Boolean)

    fun onLockStateChange(isLock: Boolean)

    fun onMediaControllerVisibleChange(visibility: Int)

    fun onProgressChange(currentProgress: Long, totalDuration: Long)

    fun onBufferPercentChange(percent: Int)

    fun getDuration(): Long?

    fun getCurrentPosition(): Long?

    fun seekTo(pos: Long)

    fun setDataSource(url: String)
}