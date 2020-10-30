package com.st.stplayer_ijk

import android.content.Context
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.player.IMediaPlayerFactory

class StIjkPlayerFactory : IMediaPlayerFactory {
    override fun createMediaPlayer(context: Context): IMediaPlayer {
        return StIjkPlayer(context)
    }

    companion object {
        fun create(): StIjkPlayerFactory {
            return StIjkPlayerFactory()
        }
    }
}