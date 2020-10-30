package com.st.stplayer.util

import android.util.Log
import com.st.stplayer.manager.VideoViewManager

class LogUtil private constructor() {
    companion object {
        /**
         * If print log here.
         */
        private val LOG_LEVEL =
            if (VideoViewManager.getInstance().getVideoViewConfig().isEnableLog()) 6 else 1

        private const val VERBOSE = 5
        private const val DEBUG = 4
        private const val INFO = 3
        private const val WARN = 2
        private const val ERROR = 1

        fun v(tag: String, msg: String) {
            if (LOG_LEVEL > VERBOSE) {
                Log.v(tag, msg)
            }
        }

        fun d(tag: String, msg: String) {
            if (LOG_LEVEL > DEBUG) {
                Log.d(tag, msg)
            }
        }

        fun i(tag: String, msg: String) {
            if (LOG_LEVEL > INFO) {
                Log.i(tag, msg)
            }
        }

        fun w(tag: String, msg: String) {
            if (LOG_LEVEL > WARN) {
                Log.w(tag, msg)
            }
        }

        fun e(tag: String, msg: String) {
            if (LOG_LEVEL > ERROR) {
                Log.e(tag, msg)
            }
        }
    }
}