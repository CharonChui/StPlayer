package com.st.stplayer.player

abstract class AbstractMediaPlayer : IMediaPlayer {
    protected var mOnPreparedListener: IMediaPlayer.OnPreparedListener? =
        null
    protected var mOnCompletionListener: IMediaPlayer.OnCompletionListener? =
        null
    protected var mOnBufferingUpdateListener: IMediaPlayer.OnBufferingUpdateListener? =
        null
    protected var mOnSeekCompleteListener: IMediaPlayer.OnSeekCompleteListener? =
        null
    protected var mOnVideoSizeChangedListener: IMediaPlayer.OnVideoSizeChangedListener? =
        null
    protected var mOnErrorListener: IMediaPlayer.OnErrorListener? = null
    protected var mOnInfoListener: IMediaPlayer.OnInfoListener? = null
    protected var mOnRenderedFirstFrame: IMediaPlayer.OnRenderedFirstFrame? = null
    protected var mOnProgressChangeListener: IMediaPlayer.OnProgressChangeListener? = null

    override fun setOnPreparedListener(listener: IMediaPlayer.OnPreparedListener?) {
        mOnPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: IMediaPlayer.OnCompletionListener?) {
        mOnCompletionListener = listener
    }

    override fun setOnBufferingUpdateListener(listener: IMediaPlayer.OnBufferingUpdateListener?) {
        mOnBufferingUpdateListener = listener
    }

    override fun setOnSeekCompleteListener(listener: IMediaPlayer.OnSeekCompleteListener?) {
        mOnSeekCompleteListener = listener
    }

    override fun setOnVideoSizeChangedListener(listener: IMediaPlayer.OnVideoSizeChangedListener?) {
        mOnVideoSizeChangedListener = listener
    }

    override fun setOnErrorListener(listener: IMediaPlayer.OnErrorListener?) {
        mOnErrorListener = listener
    }

    override fun setOnInfoListener(listener: IMediaPlayer.OnInfoListener?) {
        mOnInfoListener = listener
    }

    override fun setOnRenderedFirstFrame(listener: IMediaPlayer.OnRenderedFirstFrame?) {
        mOnRenderedFirstFrame = listener
    }

    override fun setOnProgressChangeListener(listener: IMediaPlayer.OnProgressChangeListener?) {
        mOnProgressChangeListener = listener
    }

    open fun notifyOnPreparedListener() {
        mOnPreparedListener?.onPrepared(this)
    }
}