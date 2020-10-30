package com.st.stplayer.player

import android.content.Context

class SystemPlayerFactory : IMediaPlayerFactory {
    companion object {
        fun create(): IMediaPlayerFactory {
            return SystemPlayerFactory()
        }
    }

    override fun createMediaPlayer(context: Context): IMediaPlayer {
        return StSystemPlayer(context)
    }
}