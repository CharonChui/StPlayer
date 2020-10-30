package com.st.stplayer.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.st.stplayer.R
import com.st.stplayer.data.VideoEntity
import java.util.*

class FeedListAdapter(
    private val mContext: Context,
    private var mData: List<VideoEntity>
) : RecyclerView.Adapter<FeedListAdapter.ViewHolder>() {
    private val mHolderMap = HashMap<Int, ViewHolder>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.feed_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val entity: VideoEntity = mData[position]
        holder.bind(entity, position)
        mHolderMap[position] = holder
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        mHolderMap.remove(holder.bindPos)
    }

    fun getHolder(position: Int): ViewHolder? {
        return mHolderMap[position]
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class ViewHolder(mRootView: View) :
        RecyclerView.ViewHolder(mRootView) {
        val videoViewContainer: FrameLayout =
            mRootView.findViewById(R.id.feed_list_item_video_container)
        private val mCoverImg: ImageView? = mRootView.findViewById(R.id.feed_list_item_video_cover)
        private val mTitleTv: TextView = mRootView.findViewById(R.id.feed_list_item_title)

        var bindPos = 0
        var mVideoEntity: VideoEntity? = null

        fun bind(entity: VideoEntity, position: Int) {
            mVideoEntity = entity
            bindPos = position
            mTitleTv.text = entity.title
            if (!TextUtils.isEmpty(entity.cover)) {
                mCoverImg?.let { Glide.with(mContext).load(entity.cover).into(it) }
            }
            mCoverImg?.setOnClickListener {
                mOnItemClickListener?.onCoverClick(this@ViewHolder, bindPos)
            }
            mTitleTv.setOnClickListener {
                mOnItemClickListener?.onTitleClick(this@ViewHolder, bindPos)
            }
        }

        fun handleStartPlay() {
            mCoverImg?.visibility = View.GONE
        }

        init {
            mRootView.tag = this
        }
    }

    interface OnItemClickListener {
        fun onCoverClick(holder: ViewHolder, position: Int)
        fun onTitleClick(holder: ViewHolder, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    private var mOnItemClickListener: OnItemClickListener? = null
}