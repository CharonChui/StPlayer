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

class MiniPagerAdapter(
    private val mContext: Context,
    private var mData: List<VideoEntity>
) : RecyclerView.Adapter<MiniPagerAdapter.ViewHolder>() {
    private val mHolderMap = HashMap<Int, ViewHolder>()
    private var mCurrentPosition = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.mini_vp_item, parent, false)
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

    fun setCurrentPosition(position: Int) {
        mCurrentPosition = position
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val videoViewContainer: FrameLayout = itemView.findViewById(R.id.mini_item_video_container)
        private val mCoverImg: ImageView? = itemView.findViewById(R.id.mini_item_cover)
        private val mTitleTv: TextView = itemView.findViewById(R.id.mini_item_video_des)
        var videoEntity: VideoEntity? = null
        var bindPos = 0

        fun bind(entity: VideoEntity, position: Int) {
            videoEntity = entity
            bindPos = position
            mTitleTv.text = entity.title
            if (!TextUtils.isEmpty(entity.cover)) {
                if (mCoverImg != null) {
                    Glide.with(mContext).load(entity.cover).into(mCoverImg)
                }
            }
        }

        fun handleStartPlay() {
            mCoverImg?.visibility = View.GONE
        }

        init {
            itemView.tag = this
        }
    }
}