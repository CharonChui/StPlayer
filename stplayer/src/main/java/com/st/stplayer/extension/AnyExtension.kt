package com.st.stplayer.extension

import android.util.Log

class AnyExtension {
    fun Any.log(msg: String) {
        Log.d(this.javaClass.simpleName, msg)
    }
}