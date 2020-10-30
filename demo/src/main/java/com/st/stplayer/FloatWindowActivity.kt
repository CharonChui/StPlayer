package com.st.stplayer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.data.TestData
import com.st.stplayer.mediacontroller.FloatWindowMediaController
import com.st.stplayer.mediacontroller.IMediaController
import com.st.stplayer_floatwindow.FloatWindowManager
import com.st.stplayer_floatwindow.util.ViewUtil
import com.st.stplayer_ui.StVodMediaController
import kotlinx.android.synthetic.main.activity_float_window.*

class FloatWindowActivity : AppCompatActivity() {
    private var mFloatWindowMediaController: IMediaController? = null
    private var mVodMediaController: IMediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_float_window)

        mVodMediaController = StVodMediaController(this)
        mFloatWindowMediaController = FloatWindowMediaController(this)

        mShowFloatWindow.setOnClickListener {
            if (requestOverlayPermission()) {
                return@setOnClickListener
            }

            // 可以新创建一个StVideoView传递给FloatWindowManager，也可以用目前的
//                STVideoView videoView = new STVideoView(FloatWindowActivity.this);
//                videoView.setMediaController(new MediaController(FloatWindowActivity.this));
//                videoView.setVideoPath(PATH);
//                videoView.start();
//                FloatWindowManager.getInstance().showFloatWindow(FloatWindowActivity.this, videoView);
            mVideoView.setMediaController(mFloatWindowMediaController)
            FloatWindowManager.getInstance().showFloatWindow(this@FloatWindowActivity, mVideoView)
        }

        mHideFloatWindow.setOnClickListener {
            if (FloatWindowManager.getInstance().isFloatWindowShowing()) {
                FloatWindowManager.getInstance().hideFloatWindow()
                ViewUtil.removeViewFormParent(mVideoView)
                mVideoView.setMediaController(mVodMediaController)
                mVideoViewContainer.addView(mVideoView)
            }
        }

        startPlay()
    }

    private fun startPlay() {
        mVideoView.setVideoPath(TestData.getSimpleData().url)
        mVideoView.setMediaController(mVodMediaController)
        mVideoView.start()
    }

    private fun requestOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                true
            } else {
                false
            }
        } else false
    }

    override fun onResume() {
        super.onResume()
        if (!FloatWindowManager.getInstance().isFloatWindowShowing()) {
            mVideoView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!FloatWindowManager.getInstance().isFloatWindowShowing()) {
            mVideoView.pause()
        }
    }

    override fun onBackPressed() {
        if (mVideoView.isFullScreen()) {
            mVideoView.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!FloatWindowManager.getInstance().isFloatWindowShowing()) {
            mVideoView.release()
        }
    }
}