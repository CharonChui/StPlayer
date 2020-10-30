package com.st.stplayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.st.stplayer.data.VideoEntity
import com.st.stplayer.holder.VideoViewHolder
import com.st.stplayer.state.PlayerState
import com.st.stplayer.util.LogUtil
import com.st.stplayer_floatwindow.util.ViewUtil
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_video_detail.*

class VideoDetailActivity : AppCompatActivity() {
    private var mVideoView: StVideoView? = null
    private lateinit var mMediaController: StVodMediaController
    private var mUrl: String? = null
    private var mCover: String? = null
    private var mTitle: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_video_detail)
        handleIntent()
        initView()
    }

    private fun handleIntent() {
        val intent = intent
        if (intent != null) {
            mUrl = intent.getStringExtra(URL)
            mCover = intent.getStringExtra(COVER)
            mTitle = intent.getStringExtra(TITLE)
        }
    }

    private fun initView() {
        setVideoView(VideoViewHolder.getInstance().videoDetailVideoView)
        if (!TextUtils.isEmpty(mCover)) {
            Glide.with(this).load(mCover).into(mCoverImage!!)
        }
        mTitleTv.text = mTitle
    }

    fun setVideoView(videoView: StVideoView?) {
        if (mVideoView != null) {
            mVideoView!!.release()
        }
        LogUtil.i(
            LOG_TAG,
            "video detail activity set video view : $videoView"
        )
        if (videoView != null) {
            LogUtil.i(
                LOG_TAG,
                "video detail activity set video view : " + videoView.getPlayerState()
            )
            mVideoView = videoView
            ViewUtil.removeViewFormParent(mVideoView)
            if (PlayerState.STATE_PAUSED == videoView.getPlayerState() || PlayerState.STATE_PREPARED == videoView.getPlayerState() || PlayerState.STATE_PLAYING == videoView.getPlayerState() || PlayerState.STATE_PREPARING == videoView.getPlayerState()
            ) {
                // continue to play
            } else {
                mVideoView!!.setVideoURI(Uri.parse(mUrl))
            }
            LogUtil.i(
                LOG_TAG,
                "video detail activity use old video view"
            )
        } else {
            mVideoView = StVideoView(this)
            mVideoView!!.setVideoURI(Uri.parse(mUrl))
            mMediaController = StVodMediaController(this)
            mVideoView!!.setMediaController(mMediaController)
            LogUtil.i(
                LOG_TAG,
                "video detail activity new video view"
            )
        }
        mVideoViewContainer.addView(mVideoView)
        mVideoView?.start()
    }

    override fun onBackPressed() {
        if (mVideoView?.isFullScreen()!!) {
            mVideoView?.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView?.release()
    }

    companion object {
        private val LOG_TAG = VideoDetailActivity::class.java.simpleName
        const val URL = "url"
        const val COVER = "cover"
        const val TITLE = "title"
        fun startActivity(context: Context, videoEntity: VideoEntity) {
            val intent = Intent(context, VideoDetailActivity::class.java)
            intent.putExtra(URL, videoEntity.url)
            intent.putExtra(COVER, videoEntity.cover)
            intent.putExtra(TITLE, videoEntity.title)
            context.startActivity(intent)
        }
    }
}