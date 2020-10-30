package com.st.stplayer.render

import android.view.View
import com.st.stplayer.type.ScaleType

class MeasureHelper {
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    @ScaleType
    private var mScaleType = 0
    private var mVideoRotationDegree = 0
    fun setVideoRotation(videoRotationDegree: Int) {
        mVideoRotationDegree = videoRotationDegree
    }

    fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    fun setScaleType(@ScaleType scaleType: Int) {
        mScaleType = scaleType
    }

    /**
     * 注意：VideoView的宽高一定要定死，否者以下算法不成立
     */
    fun doMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int): IntArray {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) { // 软解码时处理旋转信息，交换宽高
            widthMeasureSpec += heightMeasureSpec
            heightMeasureSpec = widthMeasureSpec - heightMeasureSpec
            widthMeasureSpec -= heightMeasureSpec
        }
        var width = View.MeasureSpec.getSize(widthMeasureSpec)
        var height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (mVideoHeight == 0 || mVideoWidth == 0) {
            return intArrayOf(width, height)
        }
        when (mScaleType) {
            ScaleType.SCALE_TYPE_AUTO -> if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth
            }
            ScaleType.SCALE_TYPE_ORIGINAL -> {
                width = mVideoWidth
                height = mVideoHeight
            }
            ScaleType.SCALE_TYPE_16_9 -> if (height > width / 16 * 9) {
                height = width / 16 * 9
            } else {
                width = height / 9 * 16
            }
            ScaleType.SCALE_TYPE_4_3 -> if (height > width / 4 * 3) {
                height = width / 4 * 3
            } else {
                width = height / 3 * 4
            }
            ScaleType.SCALE_TYPE_MATCH_PARENT -> {
                width = widthMeasureSpec
                height = heightMeasureSpec
            }
            ScaleType.SCALE_TYPE_CENTER_CROP -> if (mVideoWidth * height > width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else {
                height = width * mVideoHeight / mVideoWidth
            }
            else -> if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth
            }
        }
        return intArrayOf(width, height)
    }
}