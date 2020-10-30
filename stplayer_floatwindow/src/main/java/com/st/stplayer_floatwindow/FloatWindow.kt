package com.st.stplayer_floatwindow

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.WindowManager
import android.widget.FrameLayout
import com.st.stplayer_floatwindow.util.ActivityUtil

@Suppress("DEPRECATION")
class FloatWindow(context: Context, xPos: Int, yPos: Int, width: Int, height: Int) :
    FrameLayout(context) {

    private lateinit var mWindowManager: WindowManager
    private lateinit var mParams: WindowManager.LayoutParams
    private var mXPos = 0
    private var mYPos = 0
    private var mWidth = 0
    private var mHeight = 0

    //手指按下时相对于屏幕的坐标
    private var mDownRawX = 0
    private var mDownRawY = 0

    //手指按下时相对于悬浮窗的坐标
    private var mDownX = 0
    private var mDownY = 0

    init {
        mXPos = xPos
        mYPos = yPos
        mWidth = width
        mHeight = height
        mDownX = mXPos
        mDownY = mYPos
        init()
    }

    private fun init() {
        if (mWidth <= 0 || mHeight <= 0) {
            return
        }
        mWindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        mParams.format = PixelFormat.TRANSLUCENT
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        mParams.windowAnimations = R.style.FloatWindowAnimation
        mParams.gravity = Gravity.START or Gravity.TOP
        mParams.width = mWidth
        mParams.height = mHeight
        mParams.x = mXPos
        mParams.y = mYPos
    }

    fun showWindow(): Boolean {
        return if (!isAttachedToWindow) {
            mWindowManager.addView(this, mParams)
            true
        } else {
            false
        }
    }

    fun hideWindow(): Boolean {
        return if (isAttachedToWindow) {
            mWindowManager.removeViewImmediate(this)
            true
        } else {
            false
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                mDownRawX = ev.rawX.toInt()
                mDownRawY = ev.rawY.toInt()
                mDownX = ev.x.toInt()
                mDownY = (ev.y + ActivityUtil.getStatusBarHeight(context)).toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val absDeltaX = Math.abs(ev.rawX - mDownRawX)
                val absDeltaY = Math.abs(ev.rawY - mDownRawY)
                intercepted =
                    absDeltaX > ViewConfiguration.get(context).scaledTouchSlop ||
                            absDeltaY > ViewConfiguration.get(context).scaledTouchSlop
            }
        }
        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                mParams.x = x - mDownX
                mParams.y = y - mDownY
                mWindowManager.updateViewLayout(this, mParams)
                mXPos = mParams.x
                mYPos = mParams.y
            }
        }
        return super.onTouchEvent(event)
    }

}