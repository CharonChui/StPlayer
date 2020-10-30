package com.st.stplayer.mediacontroller

import android.content.Context
import com.st.stplayer.R
import com.st.stplayer_ui.StMediaController

class FloatWindowMediaController(context: Context) : StMediaController(context) {

    override fun getLayoutId(): Int {
        return R.layout.float_window_media_controller
    }
}