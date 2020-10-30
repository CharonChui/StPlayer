package com.st.stplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer_exo.StExoPlayerFactory
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_dash.*

class DashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash)
        initView()
    }

    private fun initView() {
        // exo can support dash
        mVideoView.setMediaPlayerFactory(StExoPlayerFactory())
        mVideoView.setVideoPath(TestData.getDashData().url)
        mVideoView.setMediaController(StVodMediaController(this))
        mVideoView.start()
    }

    override fun onBackPressed() {
        if (mVideoView!!.isFullScreen()) {
            mVideoView!!.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mVideoView!!.resume()
    }

    override fun onPause() {
        super.onPause()
        mVideoView!!.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView!!.release()
    }
}