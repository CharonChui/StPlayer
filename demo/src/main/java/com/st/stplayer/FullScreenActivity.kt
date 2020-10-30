package com.st.stplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer.mediacontroller.FullScreenMediaController
import kotlinx.android.synthetic.main.activity_local_video.*

class FullScreenActivity : AppCompatActivity() {
    private lateinit var mMediaController: FullScreenMediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_local_video)
        mVideoView.openFullScreen()
        mMediaController = FullScreenMediaController(this)
        mMediaController.setOnBackBtnListener(object :
            FullScreenMediaController.OnBackBtnListener {
            override fun onBack() {
                mVideoView.closeFullScreen()
                finish()
            }
        })
        startPlay()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mVideoView.closeFullScreen()
        mVideoView.release()
    }

    private fun startPlay() {
        mVideoView.setVideoPath(TestData.getSimpleData().url)
        mVideoView.setMediaController(mMediaController)
        mMediaController.setTitle(getString(R.string.full_screen_title))
        mVideoView.start()
    }

    override fun onResume() {
        super.onResume()
        mVideoView.resume()
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
    }
}