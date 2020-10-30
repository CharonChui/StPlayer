package com.st.stplayer

import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.bumptech.glide.Glide
import com.st.stplayer.adapter.FeedListAdapter
import com.st.stplayer.data.TestData
import com.st.stplayer.holder.VideoViewHolder
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.util.LogUtil
import com.st.stplayer_floatwindow.util.ViewUtil
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_feed_list.*

class FeedListActivity : AppCompatActivity() {
    private lateinit var mAdapter: FeedListAdapter
    private lateinit var mVideoView: StVideoView
    private lateinit var mMediaController: StVodMediaController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_feed_list)
        initVideoView()
        initView()
    }

    private fun initVideoView() {
        mVideoView = StVideoView(this)
//        mVideoView.setMediaPlayerFactory(StExoPlayerFactory())
        mMediaController = StVodMediaController(this)
        mVideoView.setMediaController(mMediaController)
        mVideoView.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
            override fun onError(mp: IMediaPlayer, framework_err: Int, impl_err: Int) {
                LogUtil.e(
                    LOG_TAG,
                    "on error : $framework_err"
                )
            }
        })
        mVideoView.setOnCompletionListener(object : IMediaPlayer.OnCompletionListener {
            override fun onCompletion(mp: IMediaPlayer) {
                LogUtil.e(LOG_TAG, "on completion ")
            }
        })
    }

    private fun initView() {
        mAdapter = FeedListAdapter(this, TestData.getVideoTestData())
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val count = recyclerView.childCount
                    for (i in 0 until count) {
                        val itemView = recyclerView.getChildAt(i) ?: continue
                        val holder: FeedListAdapter.ViewHolder =
                            itemView.tag as FeedListAdapter.ViewHolder
                        val rect = Rect()
                        holder.videoViewContainer.getLocalVisibleRect(rect)
                        val height: Int = holder.videoViewContainer.height
                        if (rect.top == 0 && rect.bottom == height) {
                            startPlay(holder)
                            break
                        }
                    }
                }
            }

        })
        mRecyclerView.addOnChildAttachStateChangeListener(object :
            OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                val playerContainer =
                    view.findViewById<FrameLayout>(R.id.feed_list_item_video_container)
                val childCount = playerContainer.childCount
                for (i in 0 until childCount) {
                    val v = playerContainer.getChildAt(i)
                    if (v != null && v === mVideoView && !mVideoView.isFullScreen()) {
                        if (mVideoView.isFullScreen()) {
                            mVideoView.closeFullScreen()
                        }
                        mVideoView.release()
                        playerContainer.removeViewAt(i)
                        if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }
                }
            }
        })
        mAdapter.setOnItemClickListener(object : FeedListAdapter.OnItemClickListener {
            override fun onCoverClick(holder: FeedListAdapter.ViewHolder, position: Int) {
                startPlay(holder)
            }

            override fun onTitleClick(holder: FeedListAdapter.ViewHolder, position: Int) {
                VideoViewHolder.getInstance().clearVideoDetailVideoView()
                VideoViewHolder.getInstance().videoDetailVideoView = mVideoView
                holder.mVideoEntity?.let {
                    VideoDetailActivity.startActivity(
                        this@FeedListActivity,
                        it
                    )
                }
            }
        })
        mRecyclerView.post { startPlay(mAdapter.getHolder(0)) }
    }

    private fun startPlay(holder: FeedListAdapter.ViewHolder?) {
        require(holder?.mVideoEntity != null) { "invalidate data" }
        holder?.let {
            ViewUtil.removeViewFormParent(mVideoView)
            it.videoViewContainer.addView(mVideoView)
            it.mVideoEntity?.url?.let { mVideoView.setVideoPath(it) }
            mVideoView.start()
            val coverImage = mMediaController.coverImage
            LogUtil.i(
                LOG_TAG,
                "startPlay and get cover imageview : $coverImage"
            )
            if (coverImage != null && !TextUtils.isEmpty(holder.mVideoEntity?.cover)) {
                Glide.with(this).load(holder.mVideoEntity?.cover).into(coverImage)
            }
        }
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
        VideoViewHolder.getInstance().clearVideoDetailVideoView()
    }

    companion object {
        private val LOG_TAG = FeedListActivity::class.java.simpleName
    }
}