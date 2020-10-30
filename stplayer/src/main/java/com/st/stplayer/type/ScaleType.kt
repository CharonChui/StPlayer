package com.st.stplayer.type

import androidx.annotation.IntDef

/**
 * 播放器类型
 * 修改时需同步修改values/attrs.xml
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    ScaleType.SCALE_TYPE_AUTO,
    ScaleType.SCALE_TYPE_16_9,
    ScaleType.SCALE_TYPE_4_3,
    ScaleType.SCALE_TYPE_MATCH_PARENT,
    ScaleType.SCALE_TYPE_CENTER_CROP,
    ScaleType.SCALE_TYPE_ORIGINAL
)
annotation class ScaleType {
    companion object {
        const val SCALE_TYPE_AUTO = 0
        const val SCALE_TYPE_16_9 = 1
        const val SCALE_TYPE_4_3 = 2
        const val SCALE_TYPE_MATCH_PARENT = 3
        const val SCALE_TYPE_CENTER_CROP = 4
        const val SCALE_TYPE_ORIGINAL = 5
    }
}