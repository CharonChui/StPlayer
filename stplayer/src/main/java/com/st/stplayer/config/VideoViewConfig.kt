package com.st.stplayer.config

import com.st.stplayer.player.IMediaPlayerFactory
import com.st.stplayer.player.SystemPlayerFactory
import com.st.stplayer.type.RenderType
import com.st.stplayer.type.ScaleType

class VideoViewConfig(builder: Builder) {
    @RenderType
    private val mRenderType = builder.mRenderType

    @ScaleType
    private val mScaleType = builder.mScaleType

    private var mMediaFactory: IMediaPlayerFactory

    private var mEnableLog = true

    init {
        mEnableLog = builder.mEnableLog
        mMediaFactory = builder.mMediaFactory ?: SystemPlayerFactory.create()
    }

    @RenderType
    fun getRenderType(): Int {
        return mRenderType
    }

    @ScaleType
    fun getScaleType(): Int {
        return mScaleType
    }

    fun isEnableLog(): Boolean {
        return mEnableLog
    }

    fun getMediaFactory(): IMediaPlayerFactory {
        return mMediaFactory
    }

    class Builder {
        @RenderType
        internal var mRenderType = RenderType.RENDER_TYPE_AUTO

        @ScaleType
        internal var mScaleType = ScaleType.SCALE_TYPE_AUTO

        internal var mMediaFactory: IMediaPlayerFactory? = null
        internal var mEnableLog = true

        fun setEnableLog(enable: Boolean): Builder {
            mEnableLog = enable
            return this
        }

        fun setMediaFactory(factory: IMediaPlayerFactory): Builder {
            mMediaFactory = factory
            return this
        }

        fun setScaleType(@ScaleType scaleType: Int): Builder {
            mScaleType = scaleType
            return this
        }

        fun setRenderType(@RenderType renderType: Int): Builder {
            mRenderType = renderType
            return this
        }

        fun build(): VideoViewConfig {
            return VideoViewConfig(this)
        }
    }
}