package com.st.stplayer_ui

import android.content.Context
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.st.stplayer.mediacontroller.AbstractMediaController
import com.st.stplayer.util.LogUtil
import com.st.stplayer_ui.util.ViewUtil
import java.util.*

open class StMediaController(context: Context) : AbstractMediaController(context) {
    private var mProgress: ProgressBar? = null
    private var mEndTime: TextView? = null
    private var mCurrentTime: TextView? = null
    private var mLockView: ImageView? = null
    private var mMoreMenu: ImageView? = null
    private var mDragging = false
    private var mListenersSet = false
    private var mNextListener: OnClickListener? = null
    private var mPrevListener: OnClickListener? = null
    var mFormatBuilder: StringBuilder? = null
    var mFormatter: Formatter? = null
    protected var mPauseButton: ImageView? = null
    protected var mNextButton: ImageView? = null
    protected var mPrevButton: ImageView? = null
    protected var mBackButton: View? = null
    protected var mFullScreenButton: ImageView? = null

    private var mLocked = false

    private var mTopRoot: ViewGroup? = null
    private var mCenterRoot: ViewGroup? = null
    private var mLeftRoot: ViewGroup? = null
    private var mRightRoot: ViewGroup? = null
    private var mBottomRoot: ViewGroup? = null

    override fun getLayoutId(): Int {
        return R.layout.st_ui_media_controller
    }

    override fun initControllerView(view: View) {
        super.initControllerView(view)
        mBackButton = view.findViewById(R.id.st_player_back)
        mBackButton?.setOnClickListener(mBackListener)
        mPauseButton = view.findViewById(R.id.st_player_pause)
        mPauseButton?.requestFocus()
        mPauseButton?.setOnClickListener(mPauseListener)

        // By default these are hidden. They will be enabled when setPrevNextListeners() is called
        mNextButton = view.findViewById(R.id.st_player_next)
        if (!mListenersSet) {
            mNextButton?.visibility = View.GONE
        }
        mPrevButton = view.findViewById(R.id.st_player_prev)
        if (!mListenersSet) {
            mPrevButton?.visibility = View.GONE
        }
        mFullScreenButton = view.findViewById(R.id.st_player_fullscreen)
        mFullScreenButton?.setOnClickListener(mFullScreenListener)
        mLockView = view.findViewById(R.id.st_player_lock)
        mMoreMenu = view.findViewById(R.id.st_player_more_menu)
        mLockView?.setOnClickListener(mLockListener)
        mMoreMenu?.setOnClickListener(mMoreMenuListener)
        mProgress = view.findViewById(R.id.st_player_mediacontroller_progress)
        if (mProgress != null) {
            if (mProgress is SeekBar) {
                val seeker = mProgress as SeekBar
                seeker.setOnSeekBarChangeListener(mSeekListener)
            }
            mProgress?.max = 1000
        }
        mEndTime = view.findViewById(R.id.st_player_time)
        mCurrentTime = view.findViewById(R.id.st_player_time_current)
        mFormatBuilder = java.lang.StringBuilder()
        mFormatter = Formatter(mFormatBuilder, Locale.getDefault())
        mTopRoot = view.findViewById(R.id.st_player_top_root)
        mCenterRoot = view.findViewById(R.id.st_player_center_root)
        mLeftRoot = view.findViewById(R.id.st_player_left_root)
        mRightRoot = view.findViewById(R.id.st_player_right_root)
        mBottomRoot = view.findViewById(R.id.st_player_bottom_root)
        installPrevNextListeners()
        handleFullScreenState()
        updateLockState()
    }

    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     *
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    override fun show(timeout: Int) {
        super.show(timeout)
        if (!isShowing() && mAnchor != null) {
            setProgress()
        }
        updatePausePlay()
        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        post(mShowProgress)
    }

    /**
     * Remove the controller from the screen.
     */
    override fun hide() {
        if (mAnchor == null) return
        if (isShowing()) {
            try {
                removeCallbacks(mShowProgress)
                super.hide()
            } catch (ex: IllegalArgumentException) {
                LogUtil.w("MediaController", "already removed")
            }
        }
    }

    override fun onFullScreenStateChange(fullScreen: Boolean) {
        super.onFullScreenStateChange(fullScreen)
        handleFullScreenState()
    }

    private fun handleFullScreenState() {
        if (mPlayer == null) {
            return
        }
        val fullScreen = mPlayer!!.isFullScreen()
        mFullScreenButton?.setImageResource(
            if (fullScreen) R.drawable.st_video_component_halfscreen else R.drawable.st_video_component_fullscreen
        )
        if (fullScreen) {
            ViewUtil.showView(mBackButton)
            ViewUtil.showView(mMoreMenu)
            ViewUtil.showView(mLockView)
        } else {
            ViewUtil.hideView(mBackButton)
            ViewUtil.hideView(mMoreMenu)
            ViewUtil.hideView(mLockView)
            mLocked = false
            updateLockState()
        }
    }

    private val mShowProgress: Runnable = object : Runnable {
        override fun run() {
            val pos: Long = setProgress()
            mPlayer?.let {
                if (!mDragging && isShowing() && it.isPlaying()) {
                    postDelayed(this, 1000 - pos % 1000)
                }
            }
        }
    }

    private fun stringForTime(timeMs: Long): String? {
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        mFormatBuilder?.setLength(0)
        return if (hours > 0) {
            mFormatter?.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter?.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    private fun setProgress(): Long {
        if (mPlayer == null || mDragging) {
            return 0
        }
        val position = mPlayer!!.getCurrentPosition()
        val duration = mPlayer!!.getDuration()
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                val pos = 1000L * position / duration
                mProgress!!.progress = pos.toInt()
            }
            val percent = mPlayer!!.getBufferPercentage()
            mProgress!!.secondaryProgress = percent * 10
        }
        if (mEndTime != null) mEndTime!!.text = stringForTime(duration)
        if (mCurrentTime != null) mCurrentTime!!.text = stringForTime(position)
        return position
    }

    //    @Override
    //    public boolean onTouchEvent(MotionEvent event) {
    //        switch (event.getAction()) {
    //            case MotionEvent.ACTION_DOWN:
    //                if (isShowing()) {
    //                    hide();
    //                } else {
    //                    show(0); // show until hide is called
    //                }
    //                break;
    //            case MotionEvent.ACTION_UP:
    //                if (isShowing()) {
    //                    show(mShowTimeoutMs); // start timeout
    //                }
    //                break;
    //            case MotionEvent.ACTION_CANCEL:
    //                hide();
    //                break;
    //            default:
    //                break;
    //        }
    //        return true;
    //    }
    override fun onTrackballEvent(ev: MotionEvent?): Boolean {
        show(mShowTimeoutMs)
        return false
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        val uniqueDown = (event.repeatCount == 0
                && event.action == KeyEvent.ACTION_DOWN)
        if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE
        ) {
            if (uniqueDown) {
                doPauseResume()
                show(mShowTimeoutMs)
                if (mPauseButton != null) {
                    mPauseButton!!.requestFocus()
                }
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if (uniqueDown && !mPlayer!!.isPlaying()) {
                mPlayer!!.start()
                updatePausePlay()
                show(mShowTimeoutMs)
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
            || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
        ) {
            if (uniqueDown && mPlayer!!.isPlaying()) {
                mPlayer!!.pause()
                updatePausePlay()
                show(mShowTimeoutMs)
            }
            return true
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_MUTE || keyCode == KeyEvent.KEYCODE_CAMERA
        ) {
            // don't show the controls for volume adjustment
            return super.dispatchKeyEvent(event)
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            if (uniqueDown) {
                hide()
            }
            return true
        }
        show(mShowTimeoutMs)
        return super.dispatchKeyEvent(event)
    }

    private val mBackListener =
        OnClickListener {
            if (mPlayer!!.isFullScreen()) {
                mPlayer!!.closeFullScreen()
            }
        }

    private val mPauseListener =
        OnClickListener {
            doPauseResume()
            show(mShowTimeoutMs)
        }

    private val mFullScreenListener =
        OnClickListener {
            if (mPlayer!!.isFullScreen()) {
                mPlayer!!.closeFullScreen()
            } else {
                mPlayer!!.openFullScreen()
            }
            handleFullScreenState()
        }
    private val mLockListener = OnClickListener {
        if (mLockView != null) {
            mLocked = !mLocked
            updateLockState()
        }
    }
    private val mMoreMenuListener = OnClickListener { onMoreMenuClick() }

    protected open fun onMoreMenuClick() {}

    override fun updatePausePlay() {
        if (mRoot == null || mPauseButton == null || mPlayer == null) {
            return
        }
        if (mPlayer!!.isPlaying()) {
            mPauseButton?.setImageResource(R.drawable.st_video_component_play)
        } else {
            mPauseButton?.setImageResource(R.drawable.st_video_component_pause)
        }
    }

    private fun updateLockState() {
        if (mLockView == null) {
            return
        }
        if (mLocked) {
            mLockView?.setImageResource(R.drawable.st_video_component_lock)
        } else {
            mLockView?.setImageResource(R.drawable.st_video_component_unlock)
        }
        onLockStateChange(mLocked)
        val visibility =
            if (mLocked) View.INVISIBLE else View.VISIBLE
        mTopRoot?.visibility = visibility
        mLeftRoot?.visibility = visibility
        mRightRoot?.visibility = visibility
        mCenterRoot?.visibility = visibility
        mBottomRoot?.visibility = visibility
    }

    private fun doPauseResume() {
        mPlayer?.let {
            if (it.isPlaying()) {
                pause()
            } else {
                start()
            }
            updatePausePlay()
        }
    }

    // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private val mSeekListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onStartTrackingTouch(bar: SeekBar) {
            show(3600000)
            mDragging = true

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            removeCallbacks(mShowProgress)
        }

        override fun onProgressChanged(
            bar: SeekBar,
            progress: Int,
            fromuser: Boolean
        ) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return
            }
            val duration = mPlayer!!.getDuration()
            val newposition = duration * progress / 1000L
            mPlayer?.seekTo(newposition)
            mCurrentTime?.text = stringForTime(newposition)
            mLogicControllerList?.let {
                if (it.isNotEmpty()) {
                    for (logicControl in it) {
                        logicControl.onProgressChange(progress.toLong(), duration)
                    }
                }
            }
            mComponentList?.let {
                if (it.isNotEmpty()) {
                    for (component in it) {
                        component.onProgressChange(progress.toLong(), duration)
                    }
                }
            }
        }

        override fun onStopTrackingTouch(bar: SeekBar) {
            mDragging = false
            setProgress()
            updatePausePlay()
            show(mShowTimeoutMs)

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            post(mShowProgress)
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        mPauseButton?.isEnabled = enabled
        mNextButton?.isEnabled = enabled && mNextListener != null
        mPrevButton?.isEnabled = enabled && mPrevListener != null
        mProgress?.isEnabled = enabled
    }

    override fun setDataSource(url: String) {
        super.setDataSource(url)
        mProgress?.progress = 0
    }

    private fun installPrevNextListeners() {
        mNextButton?.setOnClickListener(mNextListener)
        mNextButton?.isEnabled = mNextListener != null
        mPrevButton?.setOnClickListener(mPrevListener)
        mPrevButton?.isEnabled = mPrevListener != null
    }

    fun setPrevNextListeners(
        next: OnClickListener,
        prev: OnClickListener
    ) {
        mNextListener = next
        mPrevListener = prev
        mListenersSet = true
        if (mRoot != null) {
            installPrevNextListeners()
            mNextButton?.visibility = View.VISIBLE
            mPrevButton?.visibility = View.VISIBLE
        }
    }

    /**
     * Sets the playback controls timeout. The playback controls are automatically hidden after this
     * duration of time has elapsed without user input.
     *
     * @param showTimeoutMs The duration in milliseconds. A non-positive value will cause the controls
     * to remain visible indefinitely.
     */
    fun setShowTimeoutMs(showTimeoutMs: Int) {
        if (showTimeoutMs > 0) {
            mShowTimeoutMs = showTimeoutMs
        }
    }
}