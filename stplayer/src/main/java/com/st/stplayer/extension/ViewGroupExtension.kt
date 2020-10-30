package com.st.stplayer.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ViewGroupExtension {
    fun ViewGroup.inflate(layoutRes: Int): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, false)
    }

    fun ViewGroup.getViewsByTag(tag: String): ArrayList<View> {
        val views = ArrayList<View>()
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(child.getViewsByTag(tag))
            }

            val tagObj = child.tag
            if (tagObj != null && tagObj == tag) {
                views.add(child)
            }

        }
        return views
    }

    fun ViewGroup.removeViewsByTag(tag: String) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is ViewGroup) {
                child.removeViewsByTag(tag)
            }

            if (child.tag == tag) {
                removeView(child)
            }
        }
    }
}