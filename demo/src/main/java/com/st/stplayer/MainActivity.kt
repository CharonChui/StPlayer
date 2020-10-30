package com.st.stplayer

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer.holder.VideoViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSimpleButton.setOnClickListener {
            startActivity(Intent(this, SimpleActivity::class.java))
        }
        mPreloadButton.setOnClickListener {
            startActivity(Intent(this, PreloadActivity::class.java))
        }
        mFloatWindowButton.setOnClickListener {
            startActivity(Intent(this, FloatWindowActivity::class.java))
        }
        mVideoApiButton.setOnClickListener {
            startActivity(Intent(this, VideoApiActivity::class.java))
        }
        mFullScreenButton.setOnClickListener {
            startActivity(Intent(this, FullScreenActivity::class.java))
        }
        mLiveButton.setOnClickListener {
            startActivity(Intent(this, LiveActivity::class.java))
        }
        mMiniButton.setOnClickListener {
            startActivity(Intent(this, MiniVideoActivity::class.java))
        }
        mFeedListButton.setOnClickListener {
            startActivity(Intent(this, FeedListActivity::class.java))
        }
        mDetailButton.setOnClickListener {
            VideoViewHolder.getInstance().clearVideoDetailVideoView()
            VideoDetailActivity.startActivity(this, TestData.getVideoTestData()[0])
        }
        mPipButton.setOnClickListener {
            startActivity(Intent(this, PipActivity::class.java))
        }
        mFilterButton.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }
        mDashButton.setOnClickListener {
            startActivity(Intent(this, DashActivity::class.java))
        }
        mPreViewButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }
    }
}