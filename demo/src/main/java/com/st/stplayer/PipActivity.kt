package com.st.stplayer

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.StVideoView.OnPlayerStateChangeListener
import com.st.stplayer.data.TestData
import com.st.stplayer.data.VideoEntity
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.ActivityUtil
import com.st.stplayer_floatwindow.util.ViewUtil
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_pip.*
import java.util.*

@RequiresApi(api = Build.VERSION_CODES.O)
class PipActivity : AppCompatActivity() {
    private lateinit var mMediaController: StVodMediaController
    private var mVideoHeight = 0
    private val mPictureInPictureParamsBuilder =
        PictureInPictureParams.Builder()
    private val iWantToBeInPipModeNow = true
    private var mReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_pip)
        mMediaController = StVodMediaController(this)
        mVideoView.setMediaController(mMediaController)
        mVideoView.setOnPlayerStateChangeListener(object : OnPlayerStateChangeListener {
            override fun onPlayerStateChange(playerState: Int) {
                when (playerState) {
                    PlayerState.STATE_PLAYING -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        updatePictureInPictureActions(
                            R.drawable.st_video_component_play,
                            "pause",
                            CONTROL_TYPE_PAUSE,
                            REQUEST_PAUSE
                        )
                    }
                    PlayerState.STATE_PAUSED, PlayerState.STATE_COMPLETE -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        updatePictureInPictureActions(
                            R.drawable.st_video_component_pause,
                            "play",
                            CONTROL_TYPE_PLAY,
                            REQUEST_PLAY
                        )
                    }
                }
            }
        })
        mVideoHeight = (ActivityUtil.getScreenWidth(this) * 9f / 16f).toInt()
        val layoutParams =
            mStVideoViewContainer.layoutParams as LinearLayout.LayoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = mVideoHeight
        mStVideoViewContainer.layoutParams = layoutParams
        mEnterPip.setOnClickListener { enterPip() }
        startPlay()
    }

    override fun onBackPressed() {
        if (mVideoView.isFullScreen()) {
            mVideoView.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    private fun startPlay() {
        val simpleData: VideoEntity = TestData.getSimpleData()
        mVideoView.setVideoPath(simpleData.url)
        mVideoView.start()
    }

    override fun onPause() {
        super.onPause()
        if (isInPipMode()) {
            // Continue playback
        } else {
            // Use existing playback logic for paused Activity behavior.
            mVideoView.pause()
        }
    }

    private fun isInPipMode(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInPictureInPictureMode
        } else {
            false
        }
    }

    override fun onStop() {
        super.onStop()
        if (isInPipMode()) {
            // 不能在onPause调用了，需要放到onStop
            mVideoView.pause()
        } else {
        }
    }

    override fun onStart() {
        super.onStart()
        if (isInPipMode()) {
            // 不能在onResume调用额，需要放到onStart
            mVideoView.resume()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isInPipMode()) {
            mVideoView.resume()
        }
    }

    public override fun onUserLeaveHint() {
        enterPip()
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Calculate the aspect ratio of the PiP screen.
            val aspectRatio =
                Rational(mVideoView.width, mVideoView.height)
            mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio)
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build())
            ViewUtil.removeViewFormParent(mVideoView)
            val decorView = window.decorView as ViewGroup
            val params = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            decorView.addView(mVideoView, params)
            mVideoView.setMediaController(null)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            mReceiver = object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent
                ) {
                    if (ACTION_MEDIA_CONTROL != intent.action) {
                        return
                    }

                    // This is where we are called back from Picture-in-Picture action
                    // items.
                    val controlType =
                        intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
                    when (controlType) {
                        CONTROL_TYPE_PLAY -> mVideoView.start()
                        CONTROL_TYPE_PAUSE -> mVideoView.pause()
                    }
                }
            }
            registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
        } else {
            unregisterReceiver(mReceiver)
            mReceiver = null
            // Restore the full-screen UI.
            ViewUtil.removeViewFormParent(mVideoView)
            mStVideoViewContainer.addView(mVideoView)
            mVideoView.setMediaController(mMediaController)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
    }

    /**
     * Update the state of pause/resume action item in Picture-in-Picture mode.
     *
     * @param iconId      The icon to be used.
     * @param title       The title text.
     * @param controlType The type of the action. either [.CONTROL_TYPE_PLAY] or [                    ][.CONTROL_TYPE_PAUSE].
     * @param requestCode The request code for the [PendingIntent].
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun updatePictureInPictureActions(
        @DrawableRes iconId: Int,
        title: String?,
        controlType: Int,
        requestCode: Int
    ) {
        val actions = ArrayList<RemoteAction>()
        actions.add(
            RemoteAction(
                Icon.createWithResource(
                    this@PipActivity,
                    android.R.drawable.ic_menu_more
                ),
                getString(R.string.more),
                getString(R.string.more_description),
                PendingIntent.getActivity(
                    this@PipActivity,
                    REQUEST_MORE,
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.more_uri))
                    ),
                    0
                )
            )
        )

        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play and pause, or the PendingIntent won't
        // be properly updated.
        val intent = PendingIntent.getBroadcast(
            this@PipActivity,
            requestCode,
            Intent(ACTION_MEDIA_CONTROL).putExtra(
                EXTRA_CONTROL_TYPE,
                controlType
            ),
            0
        )
        val icon: Icon
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            icon = Icon.createWithResource(this@PipActivity, iconId)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                actions.add(RemoteAction(icon, title!!, title, intent))
            }
        }

        // Another action item. This is a fixed action.
        actions.add(
            RemoteAction(
                Icon.createWithResource(
                    this@PipActivity,
                    android.R.drawable.ic_menu_info_details
                ),
                getString(R.string.info),
                getString(R.string.info_description),
                PendingIntent.getActivity(
                    this@PipActivity,
                    REQUEST_INFO,
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.info_uri))
                    ),
                    0
                )
            )
        )
        mPictureInPictureParamsBuilder.setActions(actions)
        setPictureInPictureParams(mPictureInPictureParamsBuilder.build())
    }

    companion object {
        private const val ACTION_MEDIA_CONTROL = "media_control"
        private const val EXTRA_CONTROL_TYPE = "control_type"
        private const val CONTROL_TYPE_PLAY = 1
        private const val CONTROL_TYPE_PAUSE = 2
        private const val REQUEST_PLAY = 1
        private const val REQUEST_PAUSE = 2
        private const val REQUEST_INFO = 3
        private const val REQUEST_MORE = 4
    }
}