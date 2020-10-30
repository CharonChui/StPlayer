package com.st.stplayer.util

class DebugUtil private constructor() {
    companion object {
        fun logInStackTrace(tag: String) {
            val here = RuntimeException("here")
            here.fillInStackTrace()
            LogUtil.w(tag, "Called: $here")
        }
    }
}