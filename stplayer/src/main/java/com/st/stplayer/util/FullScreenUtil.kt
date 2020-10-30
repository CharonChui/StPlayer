package com.st.stplayer.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.ViewGroup
import com.st.stplayer.mediacontroller.IMediaController

class FullScreenUtil private constructor() {
    companion object {
        fun getDecorView(activity: Activity): ViewGroup {
            return activity.window.decorView as ViewGroup
        }


        fun getActivity(
            context: Context,
            mediaController: IMediaController?
        ): Activity? {
            var activity: Activity?
            if (mediaController != null) {
                activity = scanForActivity(mediaController.getView().context)
                if (activity == null) {
                    activity = scanForActivity(context)
                }
            } else {
                activity = scanForActivity(context)
            }
            return activity
        }

        private fun scanForActivity(context: Context): Activity? {
            if (context is Activity) {
                return context
            } else if (context is ContextWrapper) {
                return scanForActivity(context.baseContext)
            }
            return null
        }
    }
}