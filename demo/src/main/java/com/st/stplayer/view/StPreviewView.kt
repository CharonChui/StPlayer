package com.st.stplayer.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.st.stplayer.R
import com.st.stplayer_ui.util.DpUtil

class StPreviewView : RelativeLayout {
    private lateinit var mImageView: ImageView
    private lateinit var mLoadingView: ProgressBar

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_preview, this, true)
        mImageView = findViewById(R.id.preview_icon)
        mLoadingView = findViewById(R.id.preview_progressbar)
    }

    fun showPreView(url: String?, time: Long) {
        var time = time
        if (mImageView == null || TextUtils.isEmpty(url)) {
            return
        }
        val width = DpUtil.dip2px(context, 150)
        val height = DpUtil.dip2px(context, 100)
        // 不用太精准，秒为单位
        time /= 1000
        time *= 1000
        showLoading()
        Glide.with(context.applicationContext)
            .setDefaultRequestOptions(
                RequestOptions() //这里限制了只从缓存读取
                    //                                .onlyRetrieveFromCache(false)
                    .frame(1000 * time)
                    .override(width, height)
                    .dontAnimate()
                    .centerCrop()
            )
            .load(url)
            .addListener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    hideLoading()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any,
                    target: Target<Drawable?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    hideLoading()
                    return false
                }
            })
            .into(mImageView)
    }

    private fun showLoading() {
        mLoadingView.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        mLoadingView.visibility = View.GONE
    }

    private fun startDownFrame(url: String, duration: Long) {
        for (i in 1..100) {
            val time = (i * duration / 100).toInt()
            val width = DpUtil.dip2px(context, 150)
            val height = DpUtil.dip2px(context, 100)
            Glide.with(context.applicationContext)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .frame(1000 * time.toLong()) // 微秒
                        .override(width, height)
                        .centerCrop()
                )
                .load(url).preload(width, height)
        }
    }

    companion object {
        private val LOG_TAG = StPreviewView::class.java.simpleName
    }
}
/**
其它几种实现方式:

1. 通过MediaMetadataRetriever 只能获取你指定时间的附近的关键帧（Key frame）
    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    mmr.setDataSource(renderOutputFilePath);
    mmr.getFrameAtTime(1x1000x1000,OPTION_CLOSEST_SYNC );//
获取1秒附近的关键帧，注意，只是附近，获取不到精确位置的图片。但是用于预览也够了

2. 通过GLSurfaceView，拖到到某个事件点后，来onDrawFrame，这种方式比较高效。

3. FFmpeg实现，获取某个位置的picture，github上有封装好的实现此功能的库，类似和MediaMetadataRetriever一样的用法，可以更精准，高效。
链接：https://github.com/wseemann/FFmpegMediaMetadataRetriever，原理就是取某个timebase的关键帧。然后回调出去展示。
需要注意的是，取帧是个耗时的操作，需要放到子线程中

    FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
    mmr.setDataSource(mUri);

    //获取第一帧原尺寸图片
    mmrc.getFrameAtTime();

    //获取指定位置的原尺寸图片 注意这里传的timeUs是微秒
    mmrc.getFrameAtTime(timeUs, option);

    //获取指定位置指定宽高的缩略图
    mmrc.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height);

    //获取指定位置指定宽高并且旋转的缩略图
    mmrc.getScaledFrameAtTime(timeUs, MediaMetadataRetrieverCompat.OPTION_CLOSEST, width, height, rotate);
 */