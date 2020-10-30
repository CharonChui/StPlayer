package com.st.stplayer.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.view.KeyEvent
import com.st.stplayer.inter.IVolumeChangeUI

private const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"

class VolumeHelper private constructor() {
    private var mAudioManager: AudioManager? = null
    private var mOnVolumeChangeListener: OnVolumeChangeListener? = null
    private var mVolumeChangeUI: IVolumeChangeUI? = null

    private object SingletonHolder {
        val INSTANCE = VolumeHelper()
    }

    companion object {
        fun getInstance(): VolumeHelper {
            return SingletonHolder.INSTANCE
        }
    }

    fun setVolumeChangeUI(volumeChangeUI: IVolumeChangeUI?) {
        mVolumeChangeUI = volumeChangeUI
    }

    fun getSystemVolume(context: Context): Int {
        initAudioManager(context)
        return mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    private fun initAudioManager(context: Context) {
        if (mAudioManager == null) {
            mAudioManager = context.applicationContext
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
    }

    fun getSystemMaxVolume(context: Context): Int {
        initAudioManager(context)
        return mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    fun setSystemVolume(context: Context, volume: Int) {
        initAudioManager(context)
        mAudioManager!!.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            volume,
            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
        )
    }

    fun interceptVolumeChange(
        context: Context?,
        keyCode: Int
    ): Boolean {
        return if (context == null) {
            false
        } else when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                raiseVolume(context)
                showVolumeChangeView(context)
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                lowerVolume(context)
                showVolumeChangeView(context)
                true
            }
            else -> {
                dismissVolumeChangeView()
                false
            }
        }
    }

    private fun dismissVolumeChangeView() {
        mVolumeChangeUI?.dismiss()
    }

    private fun showVolumeChangeView(context: Context) {
        mVolumeChangeUI?.show(getSystemVolume(context), getSystemMaxVolume(context))
    }

    private fun raiseVolume(context: Context) {
        initAudioManager(context)
        mAudioManager!!.adjustStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    private fun lowerVolume(context: Context) {
        initAudioManager(context)
        mAudioManager!!.adjustStreamVolume(
            AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    fun setVolumeChangeListener(
        context: Context,
        listener: OnVolumeChangeListener?
    ) {
        mOnVolumeChangeListener = listener
        val filter = IntentFilter()
        filter.addAction(VOLUME_CHANGED_ACTION)
        context.applicationContext.registerReceiver(mVolumeChangeReceiver, filter)
    }

    fun clearVolumeChangeListener(context: Context) {
        context.applicationContext.unregisterReceiver(mVolumeChangeReceiver)
        mOnVolumeChangeListener = null
    }

    private val mVolumeChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == VOLUME_CHANGED_ACTION) {
                val systemVolume = getSystemVolume(context)
                if (mOnVolumeChangeListener != null) {
                    mOnVolumeChangeListener!!.onVolumeChange(systemVolume)
                }
            }
        }
    }

    interface OnVolumeChangeListener {
        fun onVolumeChange(volume: Int)
    }
}