package com.st.stplayer

import android.os.Bundle
import android.view.KeyEvent
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.st.stplayer.helper.VolumeHelper
import com.st.stplayer.type.RenderType
import com.st.stplayer.type.ScaleType
import com.st.stplayer_ui.StVodMediaController
import com.st.stplayer_ui.view.StVolumeChangeDialog
import kotlinx.android.synthetic.main.activity_video_api.*

class VideoApiActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private var mStVolumeChangeDialog: StVolumeChangeDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_api)
        supportActionBar?.hide()
        scale_auto.setOnCheckedChangeListener(this)
        scale_16_9.setOnCheckedChangeListener(this)
        scale_4_3.setOnCheckedChangeListener(this)
        scale_match_parent.setOnCheckedChangeListener(this)
        scale_center_crop.setOnCheckedChangeListener(this)
        scale_orignal.setOnCheckedChangeListener(this)

        mVideoView.setRenderType(RenderType.RENDER_TYPE_GL_SURFACE)
        mStVolumeChangeDialog = StVolumeChangeDialog(this)
        VolumeHelper.getInstance().setVolumeChangeUI(mStVolumeChangeDialog)
        startPlay()
    }

    private fun startPlay() {
        mVideoView.setVideoPath(PATH)
        mVideoView.setMediaController(StVodMediaController(this))
        mVideoView.start()
    }

    override fun onBackPressed() {
        if (mVideoView.isFullScreen()) {
            mVideoView.closeFullScreen()
            return
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        mVideoView.resume()
    }

    override fun onPause() {
        super.onPause()
        mVideoView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVideoView.release()
        VolumeHelper.getInstance().setVolumeChangeUI(null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val interceptVolumeChange =
            VolumeHelper.getInstance().interceptVolumeChange(this, keyCode)
        return if (interceptVolumeChange) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        private const val PATH =
            "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4"
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            val id = buttonView.id
            val scaleType: Int
            scaleType = when (id) {
                R.id.scale_16_9 -> ScaleType.SCALE_TYPE_16_9
                R.id.scale_4_3 -> ScaleType.SCALE_TYPE_4_3
                R.id.scale_match_parent -> ScaleType.SCALE_TYPE_MATCH_PARENT
                R.id.scale_center_crop -> ScaleType.SCALE_TYPE_CENTER_CROP
                R.id.scale_orignal -> ScaleType.SCALE_TYPE_ORIGINAL
                R.id.scale_auto -> ScaleType.SCALE_TYPE_AUTO
                else -> ScaleType.SCALE_TYPE_AUTO
            }
            mVideoView.setScaleType(scaleType)
        }
    }
}