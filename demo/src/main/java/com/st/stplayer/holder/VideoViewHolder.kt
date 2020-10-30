package com.st.stplayer.holder

import com.st.stplayer.StVideoView

class VideoViewHolder private constructor() {
    var videoDetailVideoView: StVideoView? = null

    private object SingletonHolder {
        val INSTANCE = VideoViewHolder()
    }

    fun clearVideoDetailVideoView() {
        videoDetailVideoView?.release()
        videoDetailVideoView = null
    }

    companion object {
        fun getInstance(): VideoViewHolder {
            return SingletonHolder.INSTANCE
        }
    }
}