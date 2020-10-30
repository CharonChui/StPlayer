package com.st.stplayer_exo

import android.content.Context
import com.st.stplayer.player.IMediaPlayer
import com.st.stplayer.player.IMediaPlayerFactory

class StExoPlayerFactory : IMediaPlayerFactory {
    override fun createMediaPlayer(context: Context): IMediaPlayer {
        return StExoPlayer(context)
    }

    companion object {
        fun create(): StExoPlayerFactory {
            return StExoPlayerFactory()
        }
    }
}