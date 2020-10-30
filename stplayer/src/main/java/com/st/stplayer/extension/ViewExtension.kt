package com.st.stplayer.extension

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.Toast

class ViewExtension {

    fun View.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, resId, duration).show()
    }

    fun View.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, text, duration).show()
    }

    fun View.isVisibile(): Boolean {
        return visibility == View.VISIBLE
    }

    /**
     * Extension method to get a view as bitmap.
     */
    fun View.getBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        draw(canvas)
        canvas.save()
        return bmp
    }
}