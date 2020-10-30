package com.st.stplayer.application

import android.app.Application
import android.content.Context
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.core.FlipperClient
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.st.preload.PreloadManager
import com.st.stplayer.BuildConfig
import com.st.stplayer.config.VideoViewConfig
import com.st.stplayer.manager.VideoViewManager
import com.st.stplayer.type.RenderType
import com.st.stplayer.type.ScaleType
import com.st.stplayer.util.LogUtil
import com.st.stplayer_exo.StExoPlayerFactory
import java.io.File

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initVideoView()
        initFlipper()
        initPreload()
    }

    private fun initVideoView() {
        val videoViewConfig = VideoViewConfig.Builder()
            .setEnableLog(true)
            .setMediaFactory(StExoPlayerFactory.create())
            .setRenderType(RenderType.RENDER_TYPE_TEXTURE)
            .setScaleType(ScaleType.SCALE_TYPE_AUTO)
            .build()
        VideoViewManager.getInstance().setVideoViewConfig(videoViewConfig)
    }

    private fun initFlipper() {
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client: FlipperClient = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.start()
        }
    }

    private fun initPreload() {
        PreloadManager.getInstance()
            .init(applicationContext, getCacheFile(this), 20, 100 * 1024 * 1024.toLong())
    }

    private fun getCacheFile(context: Context): File? {
        val cacheFilePath: String = (context.filesDir.absolutePath
                + File.separator + LOCAL_DIRECTORY_NAME)
        //        }
        val file = File(cacheFilePath)
        if (!file.exists()) {
            try {
                val success = file.mkdirs()
                LogUtil.i(
                    LOG_TAG,
                    "preload mkdir success? : $success"
                )
            } catch (e: Exception) {
                LogUtil.e(
                    LOG_TAG,
                    "preload mkdir exception : $e"
                )
            }
        }
        return file
    }

    companion object {
        private var LOG_TAG = MyApplication::class.java.simpleName
        private const val LOCAL_DIRECTORY_NAME = "$$\$preload$$$"
    }
}