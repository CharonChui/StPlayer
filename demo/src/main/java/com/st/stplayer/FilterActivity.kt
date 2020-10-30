package com.st.stplayer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_filter.*

class FilterActivity : AppCompatActivity(),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        initView()
    }

    private fun initView() {
        mVideoView.setVideoPath(TestData.getSimpleData().url)
        mVideoView.setMediaController(StVodMediaController(this))
        mVideoView.start()
    }

    override fun onClick(v: View) {
    }
}