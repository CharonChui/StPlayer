package com.st.stplayer.util

import android.os.Handler
import android.os.Looper

class MainThreadUtil private constructor() {
    companion object {
        private object HandlerHolder {
            val INSTANCE = Handler(Looper.getMainLooper())
        }

        fun post(runnable: Runnable) {
            HandlerHolder.INSTANCE.post(runnable)
        }

        fun postDelay(runnable: Runnable, delayMillis: Long) {
            HandlerHolder.INSTANCE.postDelayed(runnable, delayMillis)
        }

        fun cancelDelayingMessage(runnable: Runnable) {
            HandlerHolder.INSTANCE.removeCallbacks(runnable)
        }

        fun isMainThread(): Boolean {
            return Looper.getMainLooper() == Looper.myLooper()
        }

    }
}