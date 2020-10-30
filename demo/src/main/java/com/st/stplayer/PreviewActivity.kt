package com.st.stplayer

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer.data.VideoEntity
import com.st.stplayer.mediacontroller.PreviewMediaController
import com.st.stplayer.util.ActivityUtil
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : AppCompatActivity() {
    private var mVideoHeight = 0
    private lateinit var mMediaController: PreviewMediaController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_preview)
        mMediaController = PreviewMediaController(this)
        mVideoView.setMediaController(mMediaController)
        mVideoHeight = (ActivityUtil.getScreenWidth(this) * 9f / 16f).toInt()
        val layoutParams =
            mVideoView.layoutParams as LinearLayout.LayoutParams
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        layoutParams.height = mVideoHeight
        mVideoView.layoutParams = layoutParams
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
        mMediaController.setUrl(simpleData.url)
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