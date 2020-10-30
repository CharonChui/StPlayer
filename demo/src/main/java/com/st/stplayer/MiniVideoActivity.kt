package com.st.stplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.st.preload.PreloadManager
import com.st.stplayer.adapter.MiniPagerAdapter
import com.st.stplayer.data.TestData
import com.st.stplayer.data.VideoEntity
import com.st.stplayer.mediacontroller.MiniMediaController
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.util.ActivityUtil
import com.st.stplayer.util.LogUtil
import com.st.stplayer.util.StatusBarUtil
import com.st.stplayer_floatwindow.util.ViewUtil
import kotlinx.android.synthetic.main.activity_mini_video.*

class MiniVideoActivity : AppCompatActivity() {
    private lateinit var mVideoView: StVideoView
    private lateinit var mPagerAdapter: MiniPagerAdapter
    private var mMiniMediaController: MiniMediaController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtil.setFullScreen(this)
        ActivityUtil.hideTitleBar(this)
        StatusBarUtil.hideSysBar(this, window.decorView)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_mini_video)
        initVideoView()
        initView()
    }

    private fun initVideoView() {
        mVideoView = StVideoView(this)
        mVideoView.setLooping(true)
        mVideoView.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer, framework_err: Int, impl_err: Int) {
                LogUtil.e(LOG_TAG, "on error $framework_err + $impl_err")
            }
        })
        mMiniMediaController = MiniMediaController(this)
        mVideoView.setMediaController(mMiniMediaController)
    }


    private fun initView() {
        mPagerAdapter = MiniPagerAdapter(this, TestData.getMiniTestData())
        mViewPager.adapter = mPagerAdapter
        mViewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mPagerAdapter.setCurrentPosition(position)
                autoStartPlay(position)
            }
        })
    }

    private fun autoStartPlay(position: Int) {
        val holder: MiniPagerAdapter.ViewHolder? = mPagerAdapter.getHolder(position)
        holder?.let {
            it.handleStartPlay()
            mVideoView.release()
            ViewUtil.removeViewFormParent(mVideoView)
            val videoEntity: VideoEntity? = it.videoEntity
            videoEntity?.url?.let { it1 -> mVideoView.setVideoPath(it1) }
            mMiniMediaController?.setCoverImageUrl(videoEntity?.cover)
            holder.videoViewContainer.addView(mVideoView, 0)
            mVideoView.start()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mVideoView.isInPauseState()) {
            mVideoView.start()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mVideoView.isPlaying()) {
            mVideoView.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
        PreloadManager.getInstance().cancelAllPrefetch()
    }

    companion object {
        private var LOG_TAG = MiniVideoActivity::class.java.simpleName
    }
}