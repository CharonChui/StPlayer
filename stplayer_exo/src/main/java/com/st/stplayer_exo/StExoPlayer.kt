package com.st.stplayer_exo

import android.content.Context
import android.net.Uri
import android.view.Surface
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.upstream.HttpDataSource.HttpDataSourceException
import com.google.android.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException
import com.google.android.exoplayer2.video.VideoListener
import com.st.stplayer.player.AbstractMediaPlayer
import java.io.IOException


class StExoPlayer(context: Context) : AbstractMediaPlayer() {
    private var mExoPlayer: SimpleExoPlayer? = null
    private var mUri: Uri? = null
    private var isPreparing = true
    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private val mEventListener: Player.EventListener = object : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            if (isPreparing) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        notifyOnPreparedListener()
                        isPreparing = false
                    }
                    Player.STATE_READY -> {

                    }
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            if (error.type === ExoPlaybackException.TYPE_SOURCE) {
                val cause: IOException = error.sourceException
                if (cause is HttpDataSourceException) {
                    // An HTTP error occurred.
                    val httpError = cause
                    // This is the request for which the error occurred.
                    val requestDataSpec = httpError.dataSpec
                    // It's possible to find out more about the error both by casting and by
                    // querying the cause.
                    if (httpError is InvalidResponseCodeException) {
                        // Cast to InvalidResponseCodeException and retrieve the response code,
                        // message and headers.
                    } else {
                        // Try calling httpError.getCause() to retrieve the underlying cause,
                        // although note that it may be null.
                    }
                }
            }
            mOnErrorListener?.onError(this@StExoPlayer, error.type, error.type)
        }
    }
    private val mVideoListener: VideoListener =
        object : VideoListener {
            override fun onVideoSizeChanged(
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                mWidth = width
                mHeight = height
                mOnVideoSizeChangedListener?.onVideoSizeChanged(
                    this@StExoPlayer,
                    width,
                    height
                )
            }

            override fun onSurfaceSizeChanged(width: Int, height: Int) {}
            override fun onRenderedFirstFrame() {
                mOnRenderedFirstFrame?.onRenderedFirstFrame()
            }
        }

    init {
        initPlayer(context)
    }

    override fun setDataSource(
        context: Context,
        path: String
    ) {
        val uri = Uri.parse(path)
        setDataSource(context, uri)
    }

    override fun setDataSource(
        context: Context,
        uri: Uri
    ) {
        mUri = uri
        mWidth = 0
        mHeight = 0
    }

    override fun prepareAsync() {
        mExoPlayer ?: return
        mUri ?: return
        mExoPlayer?.setMediaItem(MediaItem.fromUri(mUri!!))
        mExoPlayer?.prepare()
    }

    override fun setScreenOnWhilePlaying(flag: Boolean) {}
    override fun initPlayer(context: Context) {
        mExoPlayer = SimpleExoPlayer.Builder(context).build()
        mExoPlayer?.addListener(mEventListener)
        mExoPlayer?.addVideoListener(mVideoListener)
    }

    override fun setMute() {
        mExoPlayer?.audioComponent!!.volume = 0.0f
    }

    override fun setVolume(left: Float, right: Float) {
        mExoPlayer?.volume = (left + right) / 2
    }

    override fun setLooping(isLooping: Boolean) {
        mExoPlayer?.repeatMode =
            if (isLooping) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    override fun getSpeed(): Float {
        mExoPlayer ?: return 1f
        return mExoPlayer!!.playbackParameters.speed
    }

    override fun start() {
        mExoPlayer?.play()
    }

    override fun pause() {
        mExoPlayer?.pause()
    }

    override fun stop() {
        mExoPlayer?.stop()
    }

    override fun reset() {
        mExoPlayer?.stop(true)
        mExoPlayer?.setVideoSurface(null)
    }

    override fun release() {
        mExoPlayer?.removeListener(mEventListener)
        mExoPlayer?.removeVideoListener(mVideoListener)
        mExoPlayer?.release()
        mExoPlayer = null
    }

    override fun setSpeed(speed: Float): Boolean {
        mExoPlayer ?: return false
        val parameters = PlaybackParameters(speed)
        mExoPlayer!!.setPlaybackParameters(parameters)
        return true
    }

    override fun getVideoWidth(): Int {
        return mWidth
    }

    override fun getVideoHeight(): Int {
        return mHeight
    }

    override fun isPlaying(): Boolean {
        mExoPlayer ?: return false
        val state = mExoPlayer!!.playbackState
        return when (state) {
            Player.STATE_BUFFERING, Player.STATE_READY -> mExoPlayer!!.playWhenReady
            Player.STATE_IDLE, Player.STATE_ENDED -> false
            else -> false
        }
    }

    override fun seekTo(time: Long) {
        mExoPlayer?.seekTo(time)
    }

    override fun getCurrentPosition(): Long {
        mExoPlayer ?: return 0
        return mExoPlayer!!.currentPosition
    }

    override fun setSurface(surface: Surface?) {
        mExoPlayer?.setVideoSurface(surface)
    }

    override fun getDuration(): Long {
        mExoPlayer ?: return 0
        return mExoPlayer!!.duration
    }

    override fun getBufferedPercentage(): Int {
        mExoPlayer ?: 0
        return mExoPlayer!!.bufferedPercentage
    }
}