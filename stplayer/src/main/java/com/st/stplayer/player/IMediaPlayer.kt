package com.st.stplayer.player

import android.content.Context
import android.net.Uri
import android.view.Surface

/**
 * MediaPlayer接口
 */
interface IMediaPlayer {
    fun setDataSource(context: Context, path: String)

    fun setDataSource(context: Context, uri: Uri)

    fun prepareAsync()

    fun setScreenOnWhilePlaying(flag: Boolean)

    fun initPlayer(context: Context)

    fun setSurface(surface: Surface?)

    fun setMute()

    fun setVolume(left: Float, right: Float)

    fun start()

    fun pause()

    fun stop()

    fun reset()

    fun release()

    fun setSpeed(speed: Float): Boolean

    fun getVideoWidth(): Int

    fun getVideoHeight(): Int

    fun isPlaying(): Boolean

    fun seekTo(time: Long)

    fun getCurrentPosition(): Long

    fun getDuration(): Long

    fun getBufferedPercentage(): Int

    fun setLooping(isLooping: Boolean)

    fun getSpeed(): Float

    fun setOnPreparedListener(listener: OnPreparedListener?)

    fun setOnCompletionListener(listener: OnCompletionListener?)

    fun setOnBufferingUpdateListener(listener: OnBufferingUpdateListener?)

    fun setOnSeekCompleteListener(listener: OnSeekCompleteListener?)

    fun setOnVideoSizeChangedListener(listener: OnVideoSizeChangedListener?)

    fun setOnErrorListener(listener: OnErrorListener?)

    fun setOnInfoListener(listener: OnInfoListener?)

    fun setOnRenderedFirstFrame(listener: OnRenderedFirstFrame?)

    fun setOnProgressChangeListener(listener: OnProgressChangeListener?)

    interface OnInfoListener {
        fun onInfo(mp: IMediaPlayer, what: Int, extra: Int)
    }

    interface OnErrorListener {
        fun onError(mp: IMediaPlayer, framework_err: Int, impl_err: Int)
    }

    interface OnVideoSizeChangedListener {
        fun onVideoSizeChanged(mp: IMediaPlayer, width: Int, height: Int)
    }

    interface OnSeekCompleteListener {
        fun onSeekComplete(mp: IMediaPlayer)
    }

    interface OnBufferingUpdateListener {
        fun onBufferingUpdate(mp: IMediaPlayer, percent: Int)
    }

    interface OnCompletionListener {
        fun onCompletion(mp: IMediaPlayer)
    }

    interface OnPreparedListener {
        fun onPrepared(mp: IMediaPlayer)
    }

    interface OnProgressChangeListener {
        fun onProgressChange(
            currentProgress: Long,
            totalDuration: Long
        )
    }

    interface OnRenderedFirstFrame {
        fun onRenderedFirstFrame()
    }
}