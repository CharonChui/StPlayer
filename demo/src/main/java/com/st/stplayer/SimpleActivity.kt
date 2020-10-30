package com.st.stplayer

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer.mediacontroller.SimpleMediaController
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.player.IMediaPlayer.OnProgressChangeListener
import com.st.stplayer.util.ActivityUtil
import kotlinx.android.synthetic.main.activity_simple_video.*

class SimpleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_simple_video)
        val height = (ActivityUtil.getScreenWidth(this) * 9f / 16f).toInt()
        val layoutParams = mVideoView.layoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = height
        mVideoView.layoutParams = layoutParams

        mVideoView.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: IMediaPlayer) {
                Log.i(LOG_TAG, "on completion")
            }
        })
        mVideoView.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer, framework_err: Int, impl_err: Int) {
                Log.i(LOG_TAG, "on error $framework_err + $impl_err")
            }
        })
        mVideoView.setOnVideoSizeChangedListener(object : IMediaPlayer.OnVideoSizeChangedListener {
            override fun onVideoSizeChanged(mp: IMediaPlayer, width: Int, height: Int) {
                Log.i(
                    LOG_TAG,
                    "on video size change width : $width...height...$height"
                )
            }
        })
        mVideoView.setOnProgressChangeListener(object : OnProgressChangeListener {
            override fun onProgressChange(
                currentProgress: Long,
                totalDuration: Long
            ) {
                Log.e(
                    LOG_TAG,
                    "om progress change : $currentProgress..$totalDuration"
                )
            }
        })
        mVideoView.setOnRenderedFirstFrame(object : IMediaPlayer.OnRenderedFirstFrame {
            override fun onRenderedFirstFrame() {
                Log.e(LOG_TAG, "on render first frame")
            }
        })

        mVideoView.setMediaController(SimpleMediaController(baseContext))
        mVideoView.setVideoPath(TestData.getSimpleData().url)
        mVideoView.start()
    }

    override fun onBackPressed() {
        if (mVideoView.isFullScreen()) {
            mVideoView.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onResume() {
        super.onResume()
        mVideoView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
    }

    companion object {
        private var LOG_TAG = SimpleActivity::class.java.simpleName
    }
}