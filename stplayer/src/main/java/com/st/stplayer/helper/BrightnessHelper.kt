package com.st.stplayer.helper

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

private const val MAX_VALUE = 100

class BrightnessHelper private constructor() {
    private object SingletonHolder {
        val INSTANCE = BrightnessHelper()
    }

    companion object {
        fun getInstance(): BrightnessHelper? {
            return SingletonHolder.INSTANCE
        }
    }

    /**
     * 0 ~ 1
     *
     * @param context
     * @return when fail thi will return -1;
     */
    fun getBrightness(context: Context): Float {
        val activity = scanForActivity(context) ?: return (-1).toFloat()
        val lpa = activity.window.attributes
        var mBrightnessData = lpa.screenBrightness
        if (mBrightnessData <= 0.00f) {
            mBrightnessData = 0.50f
        } else if (mBrightnessData < 0.01f) {
            mBrightnessData = 0.01f
        }
        return mBrightnessData
    }

    /**
     * 0 ~ 1
     *
     * @param context
     * @param brightness
     */
    fun setBrightness(
        context: Context,
        brightness: Float
    ) {
        val activity = scanForActivity(context) ?: return
        val lpa = activity.window.attributes
        lpa.screenBrightness = brightness
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        activity.window.attributes = lpa
    }

    fun scanForActivity(context: Context): Activity? {
        if (context is Activity) {
            return context
        } else if (context is ContextWrapper) {
            return scanForActivity(context.baseContext)
        }
        return null
    }
}