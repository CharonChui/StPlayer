package com.st.stplayer.manager

import com.st.stplayer.config.VideoViewConfig

class VideoViewManager private constructor() {
    private var mVideoViewConfig: VideoViewConfig? = null

    private object SingletonHolder {
        val INSTANCE = VideoViewManager()
    }

    companion object {
        fun getInstance(): VideoViewManager {
            return SingletonHolder.INSTANCE
        }
    }

    fun setVideoViewConfig(config: VideoViewConfig) {
        mVideoViewConfig = config
    }

    fun getVideoViewConfig(): VideoViewConfig {
        if (mVideoViewConfig == null) {
            setVideoViewConfig(VideoViewConfig.Builder().build())
        }
        return mVideoViewConfig!!
    }
}