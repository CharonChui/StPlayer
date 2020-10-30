package com.st.stplayer

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.*
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import com.st.stplayer.helper.VolumeHelper
import com.st.stplayer.inter.IMediaPlayerControl
import com.st.stplayer.manager.VideoViewManager
import com.st.stplayer.mediacontroller.IMediaController
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.player.IMediaPlayerFactory
import com.st.stplayer.render.IRender
import com.st.stplayer.render.RenderFactory
import com.st.stplayer.state.PlayerState
import com.st.stplayer.type.RenderType
import com.st.stplayer.type.ScaleType
import com.st.stplayer.util.ActivityUtil
import com.st.stplayer.util.FullScreenUtil
import com.st.stplayer.util.LogUtil
import com.st.stplayer.util.StatusBarUtil

private const val RENDER_INDEX = 0
private const val CONTROL_INDEX = 1

private var LOG_TAG = StVideoView::class.java.simpleName

class StVideoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    FrameLayout(context, attrs, defStyleAttr), IMediaPlayerControl {
    private var mOnErrorListener: IMediaPlayer.OnErrorListener? = null
    private var mOnInfoListener: IMediaPlayer.OnInfoListener? = null
    private var mOnCompletionListener: IMediaPlayer.OnCompletionListener? = null
    private var mOnBufferingUpdateListener: IMediaPlayer.OnBufferingUpdateListener? = null
    private var mOnVideoSizeChangedListener: IMediaPlayer.OnVideoSizeChangedListener? = null
    private var mOnProgressChangeListener: IMediaPlayer.OnProgressChangeListener? = null
    private var mOnRenderedFirstFrame: IMediaPlayer.OnRenderedFirstFrame? = null
    private var mOnFullScreenStateChangeListener: OnFullScreenStateChangeListener? = null
    private var mOnPlayerStateChangeListener: OnPlayerStateChangeListener? = null

    private var mContainer: FrameLayout

    private var mMediaPlayerFactory: IMediaPlayerFactory? = null
    private var mMediaPlayer: IMediaPlayer? = null
    private var mUri: Uri? = null
    private var mUrl: String? = null
    private var mHeaders: Map<String, String>? = null
    private var mSeekWhenPrepared: Long = 0
    private var mAudioManager: AudioManager? = null
    private var mAudioFocusType = AudioManager.AUDIOFOCUS_GAIN
    private var mAudioAttributes: AudioAttributes? = null
    private var mMediaController: IMediaController? = null

    @RenderType
    private var mRenderType = 0
    private var mRender: IRender? = null
    private var mRenderView: View? = null

    @ScaleType
    private var mScaleType = 0
    private var mFullScreen = false
    protected var mCurrentPosition: Long = 0
    protected var mCurrentPlayState = PlayerState.STATE_IDLE
    protected var mTargetState = PlayerState.STATE_IDLE

    private var mVideoWidth = 0
    private var mVideoHeight = 0

    private var isMute = false

    private val mProgressRunnable: Runnable = object : Runnable {
        override fun run() {
            removeCallbacks(this)
            if (isPlaying()) {
                val pos = getCurrentPosition()
                val duration = getDuration()
                mMediaController?.onProgressChange(pos, duration)
                mOnProgressChangeListener?.onProgressChange(pos, duration)
                mCurrentPosition = pos
                postDelayed(this, 1000 - pos % 1000)
            }
        }
    }

    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context) : this(context, null)

    init {
        val videoViewConfig = VideoViewManager.getInstance().getVideoViewConfig()
        if (attrs != null) {
            val typedArray: TypedArray =
                context.obtainStyledAttributes(attrs, R.styleable.STVideoView)
            try {
                val renderType = typedArray.getInteger(
                    R.styleable.STVideoView_render_type,
                    videoViewConfig.getRenderType()
                )
                val scaleType = typedArray.getInteger(
                    R.styleable.STVideoView_scale_type,
                    videoViewConfig.getScaleType()
                )
                mRenderType = renderType
                mScaleType = scaleType
            } finally {
                typedArray.recycle()
            }
        } else {
            mRenderType = videoViewConfig.getRenderType()
            mScaleType = videoViewConfig.getScaleType()
        }

        LogUtil.i(
            LOG_TAG,
            "rendertype : $mRenderType...scale type...$mScaleType"
        )
        mAudioManager =
            getContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build()

        mVideoWidth = 0
        mVideoHeight = 0

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        setPlayState(PlayerState.STATE_IDLE)
        mTargetState = PlayerState.STATE_IDLE
        mContainer = FrameLayout(getContext())
        mContainer.setBackgroundColor(Color.BLACK)
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(mContainer, params)
    }

    fun setVideoPath(path: String) {
        require(!TextUtils.isEmpty(path)) {
            "video path is empty"
        }
        mUrl = path
        setVideoURI(Uri.parse(path))
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    fun setVideoURI(uri: Uri) {
        setVideoURI(uri, null)
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     * Note that the cross domain redirection is allowed by default, but that can be
     * changed with key/value pairs through the headers parameter with
     * "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     * to disallow or allow cross domain redirection.
     */
    fun setVideoURI(
        uri: Uri,
        headers: Map<String, String>?
    ) {
        mUri = uri
        mUrl = mUri.toString()
        LogUtil.i(LOG_TAG, "url : $mUrl")
        mUri ?: return
        mHeaders = headers
        mSeekWhenPrepared = 0
        openVideo()
        requestLayout()
        invalidate()
    }

    private fun openVideo() {
        startPlay()
    }

    @Suppress("DEPRECATION")
    private fun startPlay() {
        mRender?.let {
            mContainer.removeView(it.getView())
            it.release()
        }
        release()
        mRender = RenderFactory().getRenderView(mRenderType, context)
        LogUtil.i(LOG_TAG, "Render : $mRender")
        mRenderView = mRender!!.getView()
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        mContainer.addView(mRenderView, RENDER_INDEX, params)
        if (mMediaPlayerFactory == null) {
            mMediaPlayerFactory =
                VideoViewManager.getInstance().getVideoViewConfig().getMediaFactory()
        }
        val am =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        try {
            mMediaPlayer = mMediaPlayerFactory?.createMediaPlayer(context)
            LogUtil.i(LOG_TAG, "new media player : $mMediaPlayer")
            mMediaPlayer?.setOnPreparedListener(object : IMediaPlayer.OnPreparedListener {
                override fun onPrepared(mp: IMediaPlayer) {
                    setPlayState(PlayerState.STATE_PREPARED)
                    val seekToPosition = mSeekWhenPrepared
                    if (mCurrentPosition > 0) {
                        seekTo(mCurrentPosition)
                    } else if (seekToPosition != 0L) {
                        seekTo(seekToPosition)
                    }
                    mVideoWidth = mp.getVideoWidth()
                    mVideoHeight = mp.getVideoHeight()
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        mRender?.let {
                            it.setScaleType(mScaleType)
                            it.setVideoSize(mVideoWidth, mVideoHeight)
                            if (mTargetState == PlayerState.STATE_PLAYING) {
                                start()
                                mMediaController?.show()
                            } else if (!isPlaying() && (seekToPosition != 0L || getCurrentPosition() > 0)) {
                                // Show the media controls when we're paused into a video and make 'em stick.
                                mMediaController?.show()
                            } else {

                            }
                        }
                    } else {
                        // We don't know the video size yet, but should start anyway.
                        // The video size might be reported to us later.
                        if (mTargetState == PlayerState.STATE_PLAYING) {
                            start()
                        }
                    }
                }
            })
            mMediaPlayer?.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
                override fun onCompletion(mp: IMediaPlayer) {
                    setPlayState(PlayerState.STATE_COMPLETE)
                    mTargetState = PlayerState.STATE_COMPLETE
                    mCurrentPosition = 0L
                    mMediaController?.hide()
                    mOnCompletionListener?.onCompletion(mp)
                }
            })
            mMediaPlayer?.setOnBufferingUpdateListener(object :
                IMediaPlayer.OnBufferingUpdateListener {
                override fun onBufferingUpdate(mp: IMediaPlayer, percent: Int) {
                    mMediaController?.onBufferPercentChange(percent)
                    mOnBufferingUpdateListener?.onBufferingUpdate(mp, percent)
                }
            })
            mMediaPlayer?.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
                override fun onError(
                    mp: IMediaPlayer,
                    framework_err: Int,
                    impl_err: Int
                ) {
                    LogUtil.e(LOG_TAG, "on error : $framework_err..impl_err: $impl_err")
                    setPlayState(PlayerState.STATE_ERROR)
                    mTargetState = PlayerState.STATE_ERROR
                    mMediaController?.hide()
                    mOnErrorListener?.onError(mp, framework_err, impl_err)
                }
            })
            mMediaPlayer?.setOnInfoListener(object : IMediaPlayer.OnInfoListener {
                override fun onInfo(mp: IMediaPlayer, what: Int, extra: Int) {
                    when (what) {
                        MEDIA_INFO_BUFFERING_START -> setPlayState(PlayerState.STATE_BUFFERING)
                        MEDIA_INFO_BUFFERING_END -> setPlayState(PlayerState.STATE_BUFFERED)
                        MEDIA_INFO_VIDEO_RENDERING_START -> setPlayState(PlayerState.STATE_PLAYING)
                    }
                    mOnInfoListener?.onInfo(mp, what, extra)
                }
            })
            mMediaPlayer?.setOnVideoSizeChangedListener(object :
                IMediaPlayer.OnVideoSizeChangedListener {
                override fun onVideoSizeChanged(
                    mp: IMediaPlayer,
                    width: Int,
                    height: Int
                ) {
                    LogUtil.i(LOG_TAG, "onVideoSizeChanged and width is : $width..height...$height")
                    mVideoWidth = mp.getVideoWidth()
                    mVideoHeight = mp.getVideoHeight()
                    mRender?.setScaleType(mScaleType)
                    mRender?.setVideoSize(width, height)
                    mOnVideoSizeChangedListener?.onVideoSizeChanged(mp, width, height)
                }
            })
            mMediaPlayer?.setOnRenderedFirstFrame(object : IMediaPlayer.OnRenderedFirstFrame {
                override fun onRenderedFirstFrame() {
                    mOnRenderedFirstFrame?.onRenderedFirstFrame()
                }
            })
            //            mMediaPlayer.setDataSource(getContext(), mUri);
            mMediaPlayer?.setDataSource(context, mUrl!!)
            mRender?.attachToPlayer(mMediaPlayer!!)
            mMediaPlayer?.prepareAsync()
            LogUtil.i(LOG_TAG, "MediaPlayer prepare async")
            mMediaPlayer?.setScreenOnWhilePlaying(true)
            setPlayState(PlayerState.STATE_PREPARING)
            keepScreenOn = true
            mMediaController?.setDataSource(mUrl!!)

        } catch (e: Exception) {
            LogUtil.i(LOG_TAG, "new MediaPlayer Exception : $e")
            setPlayState(PlayerState.STATE_ERROR)
            mTargetState = PlayerState.STATE_ERROR
            mOnErrorListener?.onError(mMediaPlayer!!, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0)
        }
    }

    private fun isInPlaybackState(): Boolean {
        return mMediaPlayer != null && mCurrentPlayState != PlayerState.STATE_ERROR && mCurrentPlayState != PlayerState.STATE_IDLE && mCurrentPlayState != PlayerState.STATE_PREPARING && mCurrentPlayState != PlayerState.STATE_COMPLETE
    }

    @PlayerState
    fun getPlayerState(): Int {
        return mCurrentPlayState
    }

    private fun isInIdleState(): Boolean {
        return mCurrentPlayState == PlayerState.STATE_IDLE
    }

    fun isInPauseState(): Boolean {
        return mCurrentPlayState == PlayerState.STATE_PAUSED
    }

    private fun setPlayState(@PlayerState playState: Int) {
        if (mCurrentPlayState != playState) {
            mCurrentPlayState = playState
            LogUtil.i(LOG_TAG, "setPlayState : $playState")
        }
        when (playState) {
            PlayerState.STATE_PLAYING -> post(mProgressRunnable)
            else -> removeCallbacks(mProgressRunnable)
        }
        mMediaController?.onPlayerStateChange(playState)
        mOnPlayerStateChangeListener?.onPlayerStateChange(playState)
    }

    fun setPlayBackgroundColor(@DrawableRes resId: Int) {
        mContainer.setBackgroundResource(resId)
    }

    fun setPlayBackground(background: Drawable) {
        mContainer.background = background
    }

    fun setLooping(isLooping: Boolean) {
        mMediaPlayer?.setLooping(isLooping)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isInPlaybackState()) {
            toggleMediaControlsVisibility()
        }
        return super.onTouchEvent(ev)
    }

    override fun onTrackballEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && isInPlaybackState()) {
            toggleMediaControlsVisibility()
        }
        return super.onTrackballEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val isKeyCodeSupported =
            keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE && keyCode != KeyEvent.KEYCODE_MENU && keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
            ) {
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying()) {
                    pause()
                    mMediaController?.show()
                } else {
                    start()
                    mMediaController?.hide()
                }
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying()) {
                    start()
                    mMediaController?.hide()
                }
                return true
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
            ) {
                if (mMediaPlayer != null && mMediaPlayer!!.isPlaying()) {
                    pause()
                    mMediaController?.show()
                }
                return true
            } else {
                toggleMediaControlsVisibility()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun setMediaController(controller: IMediaController?) {
        LogUtil.i(LOG_TAG, "set media controller : $controller")
        if (mMediaController != null) {
            mMediaController!!.hide()
            mMediaController!!.release()
            mMediaController = null
        }
        mMediaController = controller
        attachMediaController()
    }

    private fun attachMediaController() {
        if (mMediaController != null) {
            mMediaController!!.setMediaPlayer(this)
            val anchorView: View = this
            mMediaController!!.setAnchorView(anchorView)
            var mediaControllerIndex: Int = CONTROL_INDEX
            if (mContainer.childCount == 0) {
                mediaControllerIndex = RENDER_INDEX
            }
            val childAt = mContainer.getChildAt(mediaControllerIndex)
            if (childAt != null) {
                mContainer.removeViewAt(mediaControllerIndex)
            }
            mContainer.addView(mMediaController!!.getView(), mediaControllerIndex)
        }
    }

    private fun toggleMediaControlsVisibility() {
        mMediaController ?: return
        if (mMediaController!!.isShowing()) {
            mMediaController!!.hide()
        } else {
            mMediaController!!.show()
        }
    }

    fun setMediaPlayerFactory(factory: IMediaPlayerFactory) {
        mMediaPlayerFactory = factory
    }

    fun setRenderType(@RenderType renderType: Int) {
        mRenderType = renderType
    }

    fun setScaleType(@ScaleType scaleType: Int) {
        mScaleType = scaleType
        mRender?.setScaleType(mScaleType)
        LogUtil.d(LOG_TAG, "set scale type : $scaleType")
    }

    fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener) {
        mOnErrorListener = listener
    }

    fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener) {
        mOnInfoListener = listener
    }

    fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener) {
        mOnVideoSizeChangedListener = listener
    }

    fun setOnProgressChangeListener(listener: IMediaPlayer.OnProgressChangeListener) {
        mOnProgressChangeListener = listener
    }

    fun setOnRenderedFirstFrame(listener: IMediaPlayer.OnRenderedFirstFrame) {
        mOnRenderedFirstFrame = listener
    }

    fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener) {
        mOnCompletionListener = listener
    }

    fun setOnBufferingUpdateListener(listener: IMediaPlayer.OnBufferingUpdateListener) {
        mOnBufferingUpdateListener = listener
    }

    fun setOnFullScreenStateChangeListener(listener: OnFullScreenStateChangeListener) {
        mOnFullScreenStateChangeListener = listener
    }

    fun setOnPlayerStateChangeListener(listener: OnPlayerStateChangeListener) {
        mOnPlayerStateChangeListener = listener
    }

    override fun start() {
        if (isInPlaybackState()) {
            mMediaPlayer?.start()
            setPlayState(PlayerState.STATE_PLAYING)
        }
        mTargetState = PlayerState.STATE_PLAYING
    }

    override fun replay(resetCurrentPosition: Boolean) {
        if (resetCurrentPosition) {
            mCurrentPosition = 0
        }
        startPlay()
        start()
        keepScreenOn = true
    }

    @Suppress("DEPRECATION")
    override fun pause() {
        if (isInPlaybackState() && canPause()) {
            mMediaPlayer!!.pause()
            keepScreenOn = false
            setPlayState(PlayerState.STATE_PAUSED)
        }
        mTargetState = PlayerState.STATE_PAUSED
        val am =
            context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.abandonAudioFocus(null)
    }

    override fun release() {
        LogUtil.i(LOG_TAG, "release")
        mMediaPlayer?.let {
            it.reset()
            it.release()
            mMediaPlayer = null
        }


        if (mRenderView != null) {
            mContainer.removeView(mRenderView)
            mRender?.release()
            mRenderView = null
            mRender = null
        }
        if (mAudioManager != null && mAudioFocusType != AudioManager.AUDIOFOCUS_NONE) {
            mAudioManager!!.abandonAudioFocus(null)
        }

        setPlayState(PlayerState.STATE_IDLE)
        mTargetState = PlayerState.STATE_IDLE
        mCurrentPosition = 0L
    }

    override fun resume() {
        if (mMediaPlayer != null && !mMediaPlayer!!.isPlaying() && isInPlaybackState()) {
            mMediaPlayer!!.start()
            keepScreenOn = true
            setPlayState(PlayerState.STATE_PLAYING)
        }
    }

    override fun getDuration(): Long {
        // only can get when playback state, -38 error
        return if (isInPlaybackState()) {
            mMediaPlayer!!.getDuration()
        } else 0
    }

    override fun getCurrentPosition(): Long {
        if (isInPlaybackState()) {
            mCurrentPosition = mMediaPlayer!!.getCurrentPosition()
            return mCurrentPosition
        }
        return 0
    }

    override fun seekTo(pos: Long) {
        mSeekWhenPrepared = if (isInPlaybackState()) {
            mMediaPlayer!!.seekTo(pos)
            0
        } else {
            pos
        }
    }

    override fun isPlaying(): Boolean {
        mMediaPlayer ?: return false
        return mMediaPlayer!!.isPlaying()
    }

    override fun getBufferPercentage(): Int {
        mMediaPlayer ?: return 0
        return mMediaPlayer!!.getBufferedPercentage()
    }

    override fun canPause(): Boolean {
        mMediaPlayer ?: return false
        return mMediaPlayer!!.isPlaying()
    }

    override fun openFullScreen() {
        if (mFullScreen) {
            return
        }

        val activity = FullScreenUtil.getActivity(context, mMediaController)
        activity?.let {
            val decorView = FullScreenUtil.getDecorView(it)
            setFullScreen(true)
            removeView(mContainer)
            decorView.addView(mContainer)
            StatusBarUtil.hideSysBar(activity, decorView)
            ActivityUtil.setFullScreen(it)
            ActivityUtil.setScreenHorizontal(it)
        }
    }

    private fun setFullScreen(isFullScreen: Boolean) {
        mFullScreen = isFullScreen
        mOnFullScreenStateChangeListener?.onFullScreenStateChange(mFullScreen)
        mMediaController?.onFullScreenStateChange(mFullScreen)
    }

    override fun closeFullScreen() {
        if (!mFullScreen) {
            return
        }

        val activity = FullScreenUtil.getActivity(context, mMediaController)
        activity?.let {
            val decorView = FullScreenUtil.getDecorView(it)
            setFullScreen(false)
            decorView.removeView(mContainer)
            addView(mContainer)
            StatusBarUtil.showSysBar(it, decorView)
            ActivityUtil.closeFullScreen(it)
            ActivityUtil.setScreenVertical(it)
        }
    }

    override fun isFullScreen(): Boolean {
        return mFullScreen
    }

    override fun setSpeed(speed: Float): Boolean {
        return if (isInPlaybackState()) {
            mMediaPlayer!!.setSpeed(speed)
        } else false
    }

    override fun getSpeed(): Float {
        mMediaPlayer ?: return 1f
        return mMediaPlayer!!.getSpeed()
    }

    override fun setMute() {
        setVolume(0f, 0f)
        isMute = true
    }

    override fun isMute(): Boolean {
        return isMute || VolumeHelper.getInstance().getSystemVolume(context) <= 0.05
    }

    override fun setVolume(left: Float, right: Float) {
        mMediaPlayer?.setVolume(left, right)
        isMute = left <= 0 && right <= 0
    }

    override fun takeScreenShot(): Bitmap? {
        return mRender?.takeScreenShot()
    }

    interface OnFullScreenStateChangeListener {
        fun onFullScreenStateChange(isFullScreen: Boolean)
    }

    interface OnPlayerStateChangeListener {
        fun onPlayerStateChange(@PlayerState playerState: Int)
    }
}