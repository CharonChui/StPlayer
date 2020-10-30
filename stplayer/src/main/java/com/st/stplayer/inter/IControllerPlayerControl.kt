package com.st.stplayer.inter

import com.st.stplayer.state.PlayerState

/**
 * MediaController功能接口，供LogicController和Component使用
 */
interface IControllerPlayerControl {
    fun replay(resetCurrentPosition: Boolean)

    fun start()

    fun pause()

    fun onPlayerStateChange(@PlayerState playerState: Int)

    fun onLockStateChange(isLock: Boolean)

    fun getDuration(): Long

    fun getCurrentPosition(): Long

    fun seekTo(pos: Long)

    fun hideMediaController()
}