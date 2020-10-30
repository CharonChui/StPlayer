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