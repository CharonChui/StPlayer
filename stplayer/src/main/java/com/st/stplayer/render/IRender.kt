package com.st.stplayer.render

import android.graphics.Bitmap
import android.view.View
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.type.ScaleType

interface IRender {
    /**
     * 关联AbstractPlayer
     */
    fun attachToPlayer(player: IMediaPlayer)

    /**
     * 设置视频宽高
     * @param videoWidth 宽
     * @param videoHeight 高
     */
    fun setVideoSize(videoWidth: Int, videoHeight: Int)

    /**
     * 设置视频旋转角度
     * @param degree 角度值
     */
    fun setVideoRotation(degree: Int)

    /**
     * 设置screen scale type
     * @param scaleType 类型
     */
    fun setScaleType(@ScaleType scaleType: Int)

    /**
     * 获取真实的RenderView
     */
    fun getView(): View

    /**
     * 截图
     */
    fun takeScreenShot(): Bitmap?

    /**
     * 释放资源
     */
    fun release()
}