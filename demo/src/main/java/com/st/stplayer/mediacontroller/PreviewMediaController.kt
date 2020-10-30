package com.st.stplayer.mediacontroller

import android.content.Context
import com.st.stplayer.component.PreviewGestureComponent
import com.st.stplayer_ui.StGestureController
import com.st.stplayer_ui.component.*

class PreviewMediaController(context: Context) : StGestureController(context) {
    private lateinit var mPreviewGestureComponent: PreviewGestureComponent


    override fun onInitComponent() {
        super.onInitComponent()
        addComponent(StCompletionComponent(context))
        addComponent(StErrorComponent(context))
        addComponent(StLoadingComponent(context))
        addComponent(StPrepareComponent(context))
        addComponent(StBottomProgressComponent(context))
        mPreviewGestureComponent = PreviewGestureComponent(context)
        addComponent(mPreviewGestureComponent)
    }

    fun setUrl(url: String?) {
        mPreviewGestureComponent.setUrl(url)
    }
}