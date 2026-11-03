package com.pejmands.pejkeyboard

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.LinearLayout
import kotlin.math.abs

internal class GestureKeyboardLayout(
    context: Context,
    private val onGesture: (KeyboardGestureDirection, Int, Boolean) -> Unit
) : LinearLayout(context) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private val stepDistance = maxOf(
        touchSlop * 2,
        (32f * resources.displayMetrics.density).toInt()
    ).toFloat()

    private var downX = 0f
    private var downY = 0f
    private var gestureStarted = false
    private var lastDirection: KeyboardGestureDirection? = null
    private var lastStepCount = 0
    private var feedbackSent = false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                gestureStarted = false
                lastDirection = null
                lastStepCount = 0
                feedbackSent = false
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!gestureStarted &&
                    (abs(event.x - downX) > touchSlop || abs(event.y - downY) > touchSlop)
                ) {
                    gestureStarted = true
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                return gestureStarted
            }
        }

        return gestureStarted
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                if (gestureStarted) {
                    dispatchGesture(event.x, event.y)
                }
                if (event.actionMasked == MotionEvent.ACTION_UP) {
                    resetGesture()
                }
            }

            MotionEvent.ACTION_CANCEL -> resetGesture()
        }
        return true
    }

    private fun dispatchGesture(x: Float, y: Float) {
        val deltaX = x - downX
        val deltaY = y - downY
        val direction = if (abs(deltaX) >= abs(deltaY)) {
            if (deltaX < 0f) KeyboardGestureDirection.LEFT else KeyboardGestureDirection.RIGHT
        } else {
            if (deltaY < 0f) KeyboardGestureDirection.UP else KeyboardGestureDirection.DOWN
        }
        val distance = if (direction == KeyboardGestureDirection.LEFT ||
            direction == KeyboardGestureDirection.RIGHT
        ) {
            abs(deltaX)
        } else {
            abs(deltaY)
        }
        val stepCount = (distance / stepDistance).toInt()

        if (direction != lastDirection) {
            lastDirection = direction
            lastStepCount = 0
            feedbackSent = false
        }

        val newSteps = stepCount - lastStepCount
        if (newSteps > 0) {
            onGesture(direction, newSteps, !feedbackSent)
            feedbackSent = true
            lastStepCount = stepCount
        }
    }

    private fun resetGesture() {
        gestureStarted = false
        lastDirection = null
        lastStepCount = 0
        feedbackSent = false
    }
}
