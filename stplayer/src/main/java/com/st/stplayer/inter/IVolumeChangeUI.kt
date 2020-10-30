package com.st.stplayer.inter

interface IVolumeChangeUI {
    fun show(currentVolume: Int, maxVolume: Int)

    fun dismiss()
}