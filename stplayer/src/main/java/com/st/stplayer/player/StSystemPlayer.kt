package com.st.stplayer.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.view.Surface
import com.st.stplayer.util.LogUtil

/**
 * 1. error: -2147483648
 *     <application
 *         android:networkSecurityConfig="@xml/network_security_config"
 *         android:usesCleartextTraffic="true" />
 * 2. error: -38
 *   getDuration() only can used when in play state
 *
 */
class StSystemPlayer(context: Context) : AbstractMediaPlayer() {
    private var mMediaPlayer: MediaPlayer? = null
    private var mBufferPercent = 0

    init {
        initPlayer(context)
    }

    override fun setDataSource(context: Context, path: String) {
        if (path.isEmpty()) {
            return
        }
        try {
            mMediaPlayer?.setDataSource(path)
        } catch (e: Exception) {
            LogUtil.e(LOG_TAG, e.toString())
        }
    }

    override fun setDataSource(context: Context, uri: Uri) {
        try {
            mMediaPlayer?.setDataSource(context, uri)
        } catch (e: Exception) {
            LogUtil.e(LOG_TAG, e.toString())
        }
    }

    override fun prepareAsync() {
        mMediaPlayer?.prepareAsync()
        mMediaPlayer?.setOnPreparedListener {
            mOnPreparedListener?.onPrepared(this)
        }
        mMediaPlayer?.setOnCompletionListener {
            mOnCompletionListener?.onCompletion(this)
        }
        mMediaPlayer?.setOnVideoSizeChangedListener { _, width, height ->
            mOnVideoSizeChangedListener?.onVideoSizeChanged(
                this,
                width,
                height
            )
        }
        mMediaPlayer?.setOnBufferingUpdateListener { _, percent ->
            mBufferPercent = percent
            mOnBufferingUpdateListener?.onBufferingUpdate(this, percent)
        }
        mMediaPlayer?.setOnSeekCompleteListener {
            mOnSeekCompleteListener?.onSeekComplete(this)
        }
        mMediaPlayer?.setOnErrorListener { _, what, extra ->
            LogUtil.e(LOG_TAG, "on error : $what..extra..$extra")
            mOnErrorListener?.onError(this, what, extra)
            true
        }
        mMediaPlayer?.setOnInfoListener { _, what, extra ->
            if (MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START == what) {
                mOnRenderedFirstFrame?.onRenderedFirstFrame()
            }
            mOnInfoListener?.onInfo(this, what, extra)
            true
        }
    }

    override fun setScreenOnWhilePlaying(flag: Boolean) {
        mMediaPlayer?.setScreenOnWhilePlaying(flag)
    }

    override fun initPlayer(context: Context) {
        mMediaPlayer = MediaPlayer()
    }

    override fun setSurface(surface: Surface?) {
        mMediaPlayer?.setSurface(surface)
    }

    override fun setMute() {
        setVolume(0f, 0f)
    }

    override fun setVolume(left: Float, right: Float) {
        mMediaPlayer?.setVolume(left, right)
    }

    override fun start() {
        mMediaPlayer?.start()
    }

    override fun pause() {
        mMediaPlayer?.pause()
    }

    override fun stop() {
        mMediaPlayer?.stop()
    }

    override fun reset() {
        mMediaPlayer?.reset()
    }

    override fun release() {
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    override fun setSpeed(speed: Float): Boolean {
        // only support above Android M
        return if (mMediaPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                mMediaPlayer!!.playbackParams.speed = speed
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override fun getVideoWidth(): Int {
        mMediaPlayer ?: return 0
        return mMediaPlayer!!.videoWidth
    }

    override fun getVideoHeight(): Int {
        mMediaPlayer ?: return 0
        return mMediaPlayer!!.videoHeight
    }

    override fun isPlaying(): Boolean {
        mMediaPlayer ?: return false
        return mMediaPlayer!!.isPlaying
    }

    override fun seekTo(time: Long) {
        mMediaPlayer?.seekTo(time.toInt())
    }

    override fun getCurrentPosition(): Long {
        mMediaPlayer ?: return 0
        return mMediaPlayer!!.currentPosition.toLong()
    }

    override fun getDuration(): Long {
        mMediaPlayer ?: return 0
        return mMediaPlayer!!.duration.toLong()
    }

    override fun getBufferedPercentage(): Int {
        return mBufferPercent
    }

    override fun setLooping(isLooping: Boolean) {
        mMediaPlayer?.isLooping = isLooping
    }

    override fun getSpeed(): Float {
        return 1f
    }

    companion object {
        private var LOG_TAG = StSystemPlayer::class.java.simpleName
    }
}