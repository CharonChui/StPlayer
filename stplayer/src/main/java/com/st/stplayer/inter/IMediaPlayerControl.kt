package com.st.stplayer.inter

import android.graphics.Bitmap

/**
 * VideoView接口
 */
interface IMediaPlayerControl {
    fun start()

    fun replay(resetCurrentPosition: Boolean)

    fun pause()

    fun release()

    fun resume()

    fun getDuration(): Long

    fun getCurrentPosition(): Long

    fun seekTo(pos: Long)

    fun isPlaying(): Boolean

    fun getBufferPercentage(): Int

    fun canPause(): Boolean

    fun openFullScreen()

    fun closeFullScreen()

    fun isFullScreen(): Boolean

    fun setSpeed(speed: Float): Boolean

    fun getSpeed(): Float

    fun setMute()

    fun isMute(): Boolean

    fun setVolume(left: Float, right: Float)

    fun takeScreenShot(): Bitmap?
}