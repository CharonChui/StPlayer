package com.st.stplayer_ui.util

import android.view.View

class ViewUtil private constructor() {
    companion object {
        fun showView(view: View?) {
            if (view == null) {
                return
            }
            view.visibility = View.VISIBLE
        }

        fun hideView(view: View?) {
            if (view == null) {
                return
            }
            view.visibility = View.GONE
        }
    }
}