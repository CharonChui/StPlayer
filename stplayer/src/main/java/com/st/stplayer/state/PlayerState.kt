package com.st.stplayer.state

import androidx.annotation.IntDef

@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
@IntDef(
    PlayerState.STATE_ERROR,
    PlayerState.STATE_IDLE,
    PlayerState.STATE_PREPARING,
    PlayerState.STATE_PREPARED,
    PlayerState.STATE_PLAYING,
    PlayerState.STATE_PAUSED,
    PlayerState.STATE_COMPLETE,
    PlayerState.STATE_BUFFERING,
    PlayerState.STATE_BUFFERED
)
annotation class PlayerState {
    companion object {
        const val STATE_ERROR = -1
        const val STATE_IDLE = 0
        const val STATE_PREPARING = 1
        const val STATE_PREPARED = 2
        const val STATE_PLAYING = 3
        const val STATE_PAUSED = 4
        const val STATE_COMPLETE = 5
        const val STATE_BUFFERING = 6
        const val STATE_BUFFERED = 7
    }
}