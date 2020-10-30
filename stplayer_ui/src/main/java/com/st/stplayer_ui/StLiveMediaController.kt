package com.st.stplayer_ui

import android.content.Context
import com.st.stplayer_ui.component.StCompletionComponent
import com.st.stplayer_ui.component.StErrorComponent
import com.st.stplayer_ui.component.StLoadingComponent
import com.st.stplayer_ui.component.StPrepareComponent

class StLiveMediaController(context: Context) : StMediaController(context) {

    override fun onInitComponent() {
        super.onInitComponent()
        addComponent(StCompletionComponent(context))
        addComponent(StErrorComponent(context))
        addComponent(StLoadingComponent(context))
        addComponent(StPrepareComponent(context))
    }

    override fun getLayoutId(): Int {
        return R.layout.st_ui_live_media_controller
    }
}
