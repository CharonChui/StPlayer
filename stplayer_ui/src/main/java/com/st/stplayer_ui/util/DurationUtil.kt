package com.st.stplayer_ui.util

import java.util.*

class DurationUtil private constructor() {
    companion object {
        fun stringForTime(timeMs: Long): String? {
            val totalSeconds = timeMs / 1000
            val seconds = totalSeconds % 60
            val minutes = totalSeconds / 60 % 60
            val hours = totalSeconds / 3600
            return if (hours > 0) {
                String.format(
                    Locale.getDefault(),
                    "%d:%02d:%02d",
                    hours,
                    minutes,
                    seconds
                )
            } else {
                String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    minutes,
                    seconds
                )
            }
        }
    }
}