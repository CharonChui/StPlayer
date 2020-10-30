package com.st.stplayer.util

import android.app.Activity
import android.os.Build
import android.view.View

class StatusBarUtil private constructor() {
    companion object {
        fun hideSysBar(activity: Activity?, decorView: View?) {
            if (activity == null || decorView == null) {
                return
            }
            var uiOptions = decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            decorView.systemUiVisibility = uiOptions
        }

        fun showSysBar(activity: Activity?, decorView: View?) {
            if (activity == null || decorView == null) {
                return
            }
            var uiOptions = decorView.systemUiVisibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                uiOptions = uiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }
            decorView.systemUiVisibility = uiOptions
        }
    }
}