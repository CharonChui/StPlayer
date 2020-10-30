package com.st.stplayer.mediacontroller

import android.view.View
import com.st.stplayer.component.IComponent
import com.st.stplayer.inter.IMediaPlayerControl
import com.st.stplayer.logiccontroller.ILogicController
import com.st.stplayer.state.PlayerState

/**
 * MediaController的接口
 * VideoView.setMediaController()
 */
interface IMediaController {
    fun setMediaPlayer(player: IMediaPlayerControl)

    fun setAnchorView(view: View)

    fun hide()

    fun show()

    fun isShowing(): Boolean

    fun getView(): View

    fun onPlayerStateChange(@PlayerState playerState: Int)

    fun onFullScreenStateChange(fullScreen: Boolean)

    fun addLogicControl(logicController: ILogicController)

    fun removeLogicControl(logicController: ILogicController)

    fun clearLogicControl()

    fun addComponent(component: IComponent)

    fun removeComponent(component: IComponent)

    fun clearComponent()

    fun onProgressChange(currentProgress: Long, totalDuration: Long)

    fun onBufferPercentChange(percent: Int)

    fun getDuration(): Long

    fun getCurrentPosition(): Long

    fun release()

    fun setDataSource(url: String)
}