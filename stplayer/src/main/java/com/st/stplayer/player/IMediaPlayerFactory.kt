package com.st.stplayer.player

import android.content.Context

interface IMediaPlayerFactory {
    fun createMediaPlayer(context: Context): IMediaPlayer
}