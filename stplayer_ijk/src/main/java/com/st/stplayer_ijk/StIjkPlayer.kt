package com.st.stplayer_ijk

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.Surface
import com.st.stplayer.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException

class StIjkPlayer(context: Context) :
    AbstractMediaPlayer() {
    private var mIjkMediaPlayer: IjkMediaPlayer? = null
    private var mBufferedPercent = 0
    private val onErrorListener =
        IMediaPlayer.OnErrorListener { _, framework_err, impl_err ->
            mOnErrorListener?.onError(this@StIjkPlayer, framework_err, impl_err)
            return@OnErrorListener true
        }
    private val onCompletionListener =
        IMediaPlayer.OnCompletionListener {
            mOnCompletionListener?.onCompletion(this@StIjkPlayer)
        }
    private val onInfoListener =
        IMediaPlayer.OnInfoListener { _, what, extra ->
            if (IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                mOnRenderedFirstFrame?.onRenderedFirstFrame()
            }
            mOnInfoListener?.onInfo(this@StIjkPlayer, what, extra)
            return@OnInfoListener true
        }
    private val onBufferingUpdateListener =
        IMediaPlayer.OnBufferingUpdateListener { _, percent ->
            mBufferedPercent = percent
            mOnBufferingUpdateListener?.onBufferingUpdate(this@StIjkPlayer, percent)
        }
    private val onPreparedListener =
        IMediaPlayer.OnPreparedListener {
            mOnPreparedListener?.onPrepared(this@StIjkPlayer)
        }
    private val onVideoSizeChangedListener =
        IMediaPlayer.OnVideoSizeChangedListener { iMediaPlayer, i, i1, i2, i3 ->
            val videoWidth = iMediaPlayer.videoWidth
            val videoHeight = iMediaPlayer.videoHeight
            mOnVideoSizeChangedListener?.onVideoSizeChanged(
                this@StIjkPlayer,
                videoWidth,
                videoHeight
            )
        }

    override fun initPlayer(context: Context) {
        mIjkMediaPlayer = IjkMediaPlayer()
        mIjkMediaPlayer?.let {
            it.setAudioStreamType(AudioManager.STREAM_MUSIC)
            it.setOnErrorListener(onErrorListener)
            it.setOnCompletionListener(onCompletionListener)
            it.setOnInfoListener(onInfoListener)
            it.setOnBufferingUpdateListener(onBufferingUpdateListener)
            it.setOnPreparedListener(onPreparedListener)
            it.setOnVideoSizeChangedListener(onVideoSizeChangedListener)
        }
    }

    override fun setDataSource(context: Context, path: String) {
        try {
            mIjkMediaPlayer?.dataSource = path
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun setDataSource(context: Context, uri: Uri) {
        try {
            mIjkMediaPlayer?.setDataSource(context, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun prepareAsync() {
        mIjkMediaPlayer?.prepareAsync()
    }

    override fun setScreenOnWhilePlaying(flag: Boolean) {
        mIjkMediaPlayer?.setScreenOnWhilePlaying(flag)
    }

    override fun setSurface(surface: Surface?) {
        mIjkMediaPlayer?.setSurface(surface)
    }

    override fun setMute() {
        setVolume(0f, 0f)
    }

    override fun setVolume(left: Float, right: Float) {
        mIjkMediaPlayer?.setVolume(left, right)
    }

    override fun start() {
        mIjkMediaPlayer?.start()
    }

    override fun pause() {
        mIjkMediaPlayer?.pause()
    }

    override fun stop() {
        mIjkMediaPlayer?.stop()
    }

    override fun reset() {
        mIjkMediaPlayer?.reset()
    }

    override fun release() {
        mIjkMediaPlayer?.setOnErrorListener(null)
        mIjkMediaPlayer?.setOnCompletionListener(null)
        mIjkMediaPlayer?.setOnInfoListener(null)
        mIjkMediaPlayer?.setOnBufferingUpdateListener(null)
        mIjkMediaPlayer?.setOnPreparedListener(null)
        mIjkMediaPlayer?.setOnVideoSizeChangedListener(null)
        mIjkMediaPlayer?.release()
    }

    override fun setSpeed(speed: Float): Boolean {
        mIjkMediaPlayer ?: return false
        mIjkMediaPlayer?.setSpeed(speed)
        return true
    }

    override fun getVideoWidth(): Int {
        mIjkMediaPlayer ?: return 0
        return mIjkMediaPlayer?.videoWidth!!
    }

    override fun getVideoHeight(): Int {
        mIjkMediaPlayer ?: return 0
        return mIjkMediaPlayer!!.videoHeight
    }

    override fun isPlaying(): Boolean {
        mIjkMediaPlayer ?: return false
        return mIjkMediaPlayer!!.isPlaying
    }

    override fun seekTo(time: Long) {
        mIjkMediaPlayer?.seekTo(time)
    }

    override fun getCurrentPosition(): Long {
        mIjkMediaPlayer ?: return 0
        return mIjkMediaPlayer!!.currentPosition
    }

    override fun getDuration(): Long {
        mIjkMediaPlayer ?: return 0
        return mIjkMediaPlayer!!.duration
    }

    override fun getBufferedPercentage(): Int {
        mIjkMediaPlayer ?: return 0
        return mBufferedPercent
    }

    override fun setLooping(isLooping: Boolean) {
        mIjkMediaPlayer?.isLooping = isLooping
    }

    override fun getSpeed(): Float {
        mIjkMediaPlayer ?: return 0f
        return mIjkMediaPlayer!!.getSpeed(0f)
    }

    companion object {
        private val LOG_TAG = StIjkPlayer::class.java.simpleName
    }

    init {
        initPlayer(context)
    }
}