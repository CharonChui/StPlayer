package com.st.stplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.st.preload.PreloadManager
import com.st.preload.cache.CacheListener
import com.st.stplayer.data.TestData
import com.st.stplayer.util.LogUtil
import kotlinx.android.synthetic.main.activity_preload.*
import java.io.File

class PreloadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_preload)
        mStartPlay.setOnClickListener {
            val mediaController =
                MediaController(this)
            mVideoView.setMediaController(mediaController)
            mVideoView.setVideoPath(
                PreloadManager.getInstance().getProxyUrl(TestData.getSimpleData().url)
            )
            mVideoView.start()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 10001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10001) {
            Toast.makeText(this, "start prefetch 300k", Toast.LENGTH_SHORT).show()
            PreloadManager.getInstance()
                .prefetch(TestData.getSimpleData().url, 300 * 1024)
            PreloadManager.getInstance().registerCacheListener(object : CacheListener {
                override fun onCacheAvailable(
                    cacheFile: File,
                    url: String,
                    percentsAvailable: Int
                ) {
                    LogUtil.i(
                        LOG_TAG,
                        "onCacheAvailable : $cacheFile..url : $url..percent...$percentsAvailable"
                    )
                }
            }, TestData.getSimpleData().url)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.stopPlayback()
    }

    companion object {
        private var LOG_TAG = PreloadActivity::class.java.simpleName
    }
}