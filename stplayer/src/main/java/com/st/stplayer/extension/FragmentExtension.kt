package com.st.stplayer.extension

import android.widget.Toast
import androidx.fragment.app.Fragment

class FragmentExtension {

    fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, resId, duration).show()
    }

    fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, duration).show()
    }
}