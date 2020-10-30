package com.st.stplayer.extension

import android.content.Context
import android.widget.Toast

class ContextExtension {

    fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, resId, duration).show()
    }

    fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, text, duration).show()
    }


}