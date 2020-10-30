package com.st.stplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.st.stplayer.R
import com.st.stplayer.StVideoView
import com.st.stplayer.data.VideoEntity
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.util.LogUtil
import com.st.stplayer_exo.StExoPlayerFactory
import com.st.stplayer_ui.StLiveMediaController
import java.util.*

class LivePagerAdapter(var mContext: Context, var mData: List<VideoEntity>) :
    RecyclerView.Adapter<LivePagerAdapter.ViewHolder>() {
    private val mHolderMap = HashMap<Int, ViewHolder>()
    private var mCurrentPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.live_vp_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entity: VideoEntity = mData[position]
        holder.bind(entity, position)
        mHolderMap[position] = holder
        LogUtil.i(LOG_TAG, "on bind view holder : $entity.title")
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        mHolderMap.remove(holder.bindPos)
        LogUtil.i(LOG_TAG, "on view recycled and release")
        holder.release()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        LogUtil.i(LOG_TAG, "on view detach from window release")
        holder.release()
    }

    fun getHolder(position: Int): ViewHolder? {
        return mHolderMap[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun onResume() {
        val holder = getHolder(mCurrentPosition)
        holder?.startPlay()
    }

    fun onPause() {
        val holder = getHolder(mCurrentPosition)
        holder?.release()
    }

    fun onDestroy() {
        val holder = getHolder(mCurrentPosition)
        holder?.release()
    }

    fun setCurrentPosition(position: Int) {
        mCurrentPosition = position
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var mVideoView: StVideoView = itemView.findViewById(R.id.mVideoView)

        init {
            mVideoView.setMediaPlayerFactory(StExoPlayerFactory())
            mVideoView.setOnErrorListener(object : IMediaPlayer.OnErrorListener {
                override fun onError(mp: IMediaPlayer, framework_err: Int, impl_err: Int) {
                    LogUtil.e(LOG_TAG, "on error : $framework_err..$impl_err")
                }
            })
        }

        var bindPos = 0
        private lateinit var mVideoEntity: VideoEntity

        fun bind(entity: VideoEntity, position: Int) {
            mVideoEntity = entity
            LogUtil.i(LOG_TAG, "live bind : $entity.title")
            bindPos = position
        }

        fun startPlay() {
            mVideoView.setVideoPath(mVideoEntity.url)
            LogUtil.i(LOG_TAG, "live start play : $mVideoEntity.title")
            mVideoView.start()
        }

        fun release() {
            mVideoView.release()
        }

        init {
            mVideoView.setMediaController(StLiveMediaController(mContext))
            itemView.tag = this
        }
    }

    companion object {
        private var LOG_TAG = LivePagerAdapter::class.java.simpleName
    }
}