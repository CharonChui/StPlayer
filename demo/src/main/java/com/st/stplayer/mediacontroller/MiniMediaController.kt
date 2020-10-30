package com.st.stplayer.mediacontroller

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.st.stplayer.R
import com.st.stplayer_ui.StMediaController
import com.st.stplayer_ui.component.StCompletionComponent
import com.st.stplayer_ui.component.StErrorComponent
import com.st.stplayer_ui.component.StLoadingComponent
import com.st.stplayer_ui.component.StPrepareComponent

class MiniMediaController(context: Context) : StMediaController(context) {
    private var mCoverImage: ImageView? = null

    override fun onInitComponent() {
        super.onInitComponent()
        addComponent(StCompletionComponent(context))
        addComponent(StErrorComponent(context))
        addComponent(StLoadingComponent(context))
        val stPrepareComponent = StPrepareComponent(context)
        mCoverImage = stPrepareComponent.coverImageView
//        addComponent(stPrepareComponent)
    }

    override fun getLayoutId(): Int {
        return R.layout.mini_media_controller
    }

    fun setCoverImageUrl(coverImageUrl: String?) {
        if (mCoverImage != null && !TextUtils.isEmpty(coverImageUrl)) {
            Glide.with(context).load(coverImageUrl).into(mCoverImage!!)
        }
    }
}