package com.st.stplayer_ui

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Toast
import com.st.stplayer_ui.component.*
import com.st.stplayer_ui.view.StMoreMenuView
import com.st.stplayer_ui.view.StMoreMenuView.OnOperationListener

class StVodMediaController(context: Context) : StGestureController(context) {
    var coverImage: ImageView? = null

    override fun onInitComponent() {
        super.onInitComponent()
        addComponent(StGestureComponent(context))
        addComponent(StCompletionComponent(context))
        addComponent(StErrorComponent(context))
        addComponent(StLoadingComponent(context))
        val stPrepareComponent = StPrepareComponent(context)
        coverImage = stPrepareComponent.coverImageView
        addComponent(stPrepareComponent)
        addComponent(StBottomProgressComponent(context))
    }

    override fun onMoreMenuClick() {
        super.onMoreMenuClick()
        val moreMenuView = StMoreMenuView(context)
        moreMenuView.setOnOperationListener(object : OnOperationListener {
            override fun onTakeScreenShot(): Bitmap? {
                val bitmap = mPlayer?.takeScreenShot()
                Toast.makeText(
                    context,
                    if (bitmap == null) "take pic fail" else "take pic success",
                    Toast.LENGTH_SHORT
                ).show()
                return bitmap
            }
        })

        mPlayer?.let {
            moreMenuView.show(this, it.isMute())
        }

    }

}