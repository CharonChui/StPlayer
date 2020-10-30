package com.st.stplayer_ui

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View.OnTouchListener
import androidx.core.view.GestureDetectorCompat
import com.st.stplayer.component.IGestureComponent
import com.st.stplayer.util.LogUtil
import kotlin.math.abs

open class StGestureController(context: Context) : StMediaController(context) {
    private var mGestureDetector: GestureDetectorCompat? = null
    private var mOnGestureListener: GestureDetector.OnGestureListener? = null
    private var mScaleGestureDetector: ScaleGestureDetector? = null
    private var mOnScaleGestureListener: OnScaleGestureListener? = null
    private var mOnTouchListener: OnTouchListener? = null
    private var mSupportGesture = true
    private var mFirstTouch = false
    private var mChangeHorizontal = false
    private var mChangeVertical = false
    private var mInScaleGesture = false

    init {
        mOnGestureListener = object : SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                LogUtil.i(
                    LOG_TAG,
                    "onScroll : $distanceX...$distanceY"
                )
                if (!mSupportGesture) {
                    return true
                }
                LogUtil.w(
                    LOG_TAG,
                    "mInScaleGesture : $mInScaleGesture"
                )
                if (mInScaleGesture) {
                    return true
                }
                val dX = e2.x - e1.x
                val dY = e2.y - e1.y
                if (abs(dX) < DISTANCE_THRESHOLD && abs(dY) < DISTANCE_THRESHOLD) {
                    return true
                }
                LogUtil.i(
                    LOG_TAG,
                    "on scroll dx : $dX...$dY"
                )
                if (mFirstTouch) {
                    mChangeHorizontal = abs(dX) >= abs(dY)
                    if (!mChangeHorizontal) {
                        mChangeVertical = true
                    }
                    if (mChangeHorizontal || mChangeVertical) {
                        onGestureStart()
                    }
                    mFirstTouch = false
                }
                if (mChangeHorizontal) {
                    changeHorizontal(dX)
                } else if (mChangeVertical) {
                    changeVertical(e1, dY)
                }
                return true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                LogUtil.i(
                    LOG_TAG,
                    "onSingleTapConfirmed"
                )
                if (isShowing()) {
                    hide()
                } else {
                    show()
                }
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                LogUtil.i(LOG_TAG, "onDoubleTap")
                mComponentList?.let {
                    if (it.isNotEmpty()) {
                        for (component in it) {
                            if (component is IGestureComponent) {
                                component.onDoubleClick()
                            }
                        }
                    }
                }
                return true
            }

            override fun onDown(e: MotionEvent): Boolean {
                LogUtil.i(LOG_TAG, "onDown")
                if (!mSupportGesture) {
                    return true
                }
                mFirstTouch = !mInScaleGesture
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                mComponentList?.let {
                    if (it.isNotEmpty()) {
                        for (component in it) {
                            if (component is IGestureComponent) {
                                component.onLongPress()
                            }
                        }
                    }
                }
            }
        }
        mGestureDetector = GestureDetectorCompat(context, mOnGestureListener)
        mOnScaleGestureListener = object : OnScaleGestureListener {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (mSupportGesture) {
                    mComponentList?.let {
                        if (it.isNotEmpty()) {
                            for (component in it) {
                                if (component is IGestureComponent) {
                                    component.onScale(detector.scaleFactor)
                                }
                            }
                        }
                    }
                }
                LogUtil.i(
                    LOG_TAG,
                    "onScale : " + detector.isInProgress
                )
                return mSupportGesture
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                LogUtil.i(
                    LOG_TAG,
                    "onScaleBegin : " + detector.isInProgress
                )
                return mSupportGesture
            }

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                onScaleStop(false)
                LogUtil.i(
                    LOG_TAG,
                    "onScaleEnd : " + detector.isInProgress
                )
            }
        }
        mScaleGestureDetector = ScaleGestureDetector(context, mOnScaleGestureListener)
        mOnTouchListener = OnTouchListener { _, event ->
            val scaleConsume = mScaleGestureDetector!!.onTouchEvent(event)
            if (mScaleGestureDetector!!.isInProgress) {
                return@OnTouchListener true
            }
            val action = event.action
            when (action) {
                MotionEvent.ACTION_UP -> LogUtil.i(
                    LOG_TAG,
                    "action up"
                )
                MotionEvent.ACTION_CANCEL -> LogUtil.i(
                    LOG_TAG,
                    "action cancel"
                )
                MotionEvent.ACTION_POINTER_DOWN -> LogUtil.i(
                    LOG_TAG,
                    "action pointer down"
                )
                MotionEvent.ACTION_POINTER_UP -> {
                    LogUtil.i(
                        LOG_TAG,
                        "action pointer up"
                    )
                    onScaleStop(false)
                }
            }
            val currentPointerCount = event.pointerCount > 1
            if (currentPointerCount) {
                mChangeHorizontal = false
                mChangeVertical = false
            }
            if (mInScaleGesture && !currentPointerCount) {
                // two to one
                onScaleStop(true)
                mFirstTouch = false
            } else if (!mInScaleGesture && currentPointerCount) {
                // one to two
                onScaleStart()
                mFirstTouch = false
            }
            mInScaleGesture = currentPointerCount
            LogUtil.i(
                LOG_TAG,
                "ontouch pointer count : " + event.pointerCount
            )
            val gestureConsume = mGestureDetector!!.onTouchEvent(event)
            LogUtil.i(
                LOG_TAG,
                "scale consume : $scaleConsume..gesture consume...$gestureConsume"
            )
            gestureConsume
        }
        setOnTouchListener(mOnTouchListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //滑动结束时事件处理
        if (!mGestureDetector!!.onTouchEvent(event)) {
            val action = event.action
            when (action) {
                MotionEvent.ACTION_UP -> onGestureStop(false)
                MotionEvent.ACTION_CANCEL -> onGestureStop(true)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun onGestureStart() {
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    if (component is IGestureComponent) {
                        component.onGestureStart()
                    }
                }
            }
        }
    }

    private fun onScaleStart() {
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    if (component is IGestureComponent) {
                        component.onScaleStart()
                    }
                }
            }
        }
    }

    private fun onGestureStop(isCancel: Boolean) {
        if (mChangeHorizontal || mChangeVertical) {
            mChangeHorizontal = false
            mChangeVertical = false
            mComponentList?.let {
                if (it.isNotEmpty()) {
                    for (component in it) {
                        if (component is IGestureComponent) {
                            component.onGestureStop(isCancel)
                        }
                    }
                }
            }
        }
    }

    private fun onScaleStop(isCancel: Boolean) {
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    if (component is IGestureComponent) {
                        component.onScaleStop(isCancel)
                    }
                }
            }
        }
    }

    fun setSupportGesture(supportGesture: Boolean) {
        mSupportGesture = supportGesture
    }

    protected fun changeHorizontal(dX: Float) {
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    if (component is IGestureComponent) {
                        component.onHorizontalSlide(dX)
                    }
                }
            }
        }
    }

    protected fun changeVertical(e1: MotionEvent, dY: Float) {
        mComponentList?.let {
            if (it.isNotEmpty()) {
                for (component in it) {
                    if (component is IGestureComponent) {
                        component.onVerticalSlide(e1, dY)
                    }
                }
            }
        }
    }

    companion object {
        private val LOG_TAG = StGestureController::class.java.simpleName
        private const val DISTANCE_THRESHOLD = 50f
    }
}