package com.st.stplayer.component

import android.view.MotionEvent

interface IGestureComponent : IComponent {
    fun onDoubleClick()

    fun onLongPress()

    fun onHorizontalSlide(dX: Float)

    fun onVerticalSlide(e1: MotionEvent, dY: Float)

    fun onGestureStart()
    fun onGestureStop(isCancel: Boolean)

    fun onScaleStart()
    fun onScale(scale: Float)
    fun onScaleStop(isCancel: Boolean)
}