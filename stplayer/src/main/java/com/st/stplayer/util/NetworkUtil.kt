package com.st.stplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

@Suppress("DEPRECATION")
class NetworkUtil private constructor() {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager
                .activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable
        }

        fun isMobile(context: Context): Boolean {
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            return (activeNetInfo != null
                    && activeNetInfo.type == ConnectivityManager.TYPE_MOBILE)
        }

        fun isWIFIActivate(context: Context): Boolean {
            return (context.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .isWifiEnabled
        }
    }
}