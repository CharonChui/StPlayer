package com.st.stplayer.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

class ActivityUtil private constructor() {
    companion object {
        private fun toggleFullScreen(activity: Activity, isFull: Boolean) {
            val window = activity.window
            val winParams = window.attributes
            val bits = WindowManager.LayoutParams.FLAG_FULLSCREEN
            if (isFull) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            window.attributes = winParams
        }

        fun setFullScreen(activity: Activity) {
            toggleFullScreen(activity, true)
        }

        fun closeFullScreen(activity: Activity) {
            toggleFullScreen(activity, false)
        }

        fun isLandScape(context: Context): Boolean {
            return context.resources
                .configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }

        fun getStatusBarHeight(activity: Activity): Int {
            return try {
                val clazz = Class.forName("com.android.internal.R\$dimen")
                val `object` = clazz.newInstance()
                val field = clazz.getField("status_bar_height")
                val dpHeight = field[`object`].toString().toInt()
                activity.resources.getDimensionPixelSize(dpHeight)
            } catch (e1: Exception) {
                e1.printStackTrace()
                0
            }
        }

        fun getScreenWidth(context: Context): Int {
            val metric = DisplayMetrics()
            val windowManager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metric)
            return metric.widthPixels
        }

        fun getScreenHeight(context: Context): Int {
            val metric = DisplayMetrics()
            val windowManager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metric)
            return metric.heightPixels
        }

        fun hideTitleBar(activity: Activity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
        }

        fun setScreenVertical(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        fun setScreenHorizontal(activity: Activity) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        fun hideSoftInput(activity: Activity) {
            activity.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
        }

        fun closeSoftInput(
            context: Context,
            focusingView: View
        ) {
            val imm = context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                focusingView.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }

        fun startApkActivity(
            ctx: Context,
            packageName: String?
        ) {
            val pm = ctx.packageManager
            val pi: PackageInfo
            try {
                pi = pm.getPackageInfo(packageName, 0)
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.setPackage(pi.packageName)
                val apps = pm.queryIntentActivities(intent, 0)
                val ri = apps.iterator().next()
                if (ri != null) {
                    val className = ri.activityInfo.name
                    intent.component = ComponentName(packageName!!, className)
                    ctx.startActivity(intent)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        fun isApplicationBroughtToBackground(context: Context): Boolean {
            val am = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = am.getRunningTasks(1)
            if (tasks != null && tasks.isNotEmpty()) {
                val topActivity = tasks[0].topActivity
                if (topActivity!!.packageName != context.packageName) {
                    return true
                }
            }
            return false
        }
    }
}