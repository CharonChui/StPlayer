package com.st.stplayer.type

import androidx.annotation.IntDef

/**
 * 播放器类型
 * 修改时需同步修改values/attrs.xml
 */
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    PlayerType.PLAYER_TYPE_AUTO,
    PlayerType.PLAYER_TYPE_EXO,
    PlayerType.PLAYER_TYPE_IJK,
    PlayerType.PLAYER_TYPE_SYS
)
annotation class PlayerType {
    companion object {
        const val PLAYER_TYPE_AUTO = 0
        const val PLAYER_TYPE_EXO = 1
        const val PLAYER_TYPE_IJK = 2
        const val PLAYER_TYPE_SYS = 3
    }
}