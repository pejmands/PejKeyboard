package com.pejmands.pejkeyboard

import android.content.Context
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.LinearLayout
import kotlin.math.abs

internal data class GestureUpdate(
    val direction: KeyboardGestureDirection,
    val steps: Int,
    val directionChanged: Boolean
)

internal class GestureDirectionTracker(
    private val touchSlop: Float,
    private val stepDistance: Float
) {
    private var currentDirection: KeyboardGestureDirection? = null
    private var pendingDirection: KeyboardGestureDirection? = null
    private var pendingDistance = 0f
    private var directionDistance = 0f
    private var lastStepCount = 0

    fun reset() {
        currentDirection = null
        pendingDirection = null
        pendingDistance = 0f
        directionDistance = 0f
        lastStepCount = 0
    }

    fun addMovement(deltaX: Float, deltaY: Float): GestureUpdate? {
        if (deltaX == 0f && deltaY == 0f) {
            return null
        }

        val candidate = if (abs(deltaX) >= abs(deltaY)) {
            if (deltaX < 0f) KeyboardGestureDirection.LEFT else KeyboardGestureDirection.RIGHT
        } else {
            if (deltaY < 0f) KeyboardGestureDirection.UP else KeyboardGestureDirection.DOWN
        }
        val movementDistance = if (
            candidate == KeyboardGestureDirection.LEFT ||
            candidate == KeyboardGestureDirection.RIGHT
        ) {
            abs(deltaX)
        } else {
            abs(deltaY)
        }

        val directionChanged = candidate != currentDirection
        if (directionChanged) {
            if (candidate != pendingDirection) {
                pendingDirection = candidate
                pendingDistance = 0f
            }
            pendingDistance += movementDistance
            if (pendingDistance <= touchSlop) {
                return null
            }

            currentDirection = candidate
            directionDistance = pendingDistance
            pendingDirection = null
            pendingDistance = 0f
            lastStepCount = 0
        } else {
            pendingDirection = null
            pendingDistance = 0f
            directionDistance += movementDistance
        }

        val stepCount = (directionDistance / stepDistance).toInt()
        val newSteps = stepCount - lastStepCount
        if (newSteps > 0) {
            lastStepCount = stepCount
        }

        return if (directionChanged || newSteps > 0) {
            GestureUpdate(candidate, newSteps, directionChanged)
        } else {
            null
        }
    }
}

internal class GestureKeyboardLayout(
    context: Context,
    private val isRepeatEnabled: () -> Boolean,
    private val onGesture: (KeyboardGestureDirection, Int, Boolean) -> Unit
) : LinearLayout(context) {

    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop.toFloat()
    private val stepDistance = maxOf(
        touchSlop * 2f,
        32f * resources.displayMetrics.density
    )
    private val directionTracker = GestureDirectionTracker(touchSlop, stepDistance)

    private var lastX = 0f
    private var lastY = 0f
    private var gestureStarted = false
    private var feedbackSent = false
    private var actionDispatched = false

    init {
        isClickable = true
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                gestureStarted = false
                directionTracker.reset()
                feedbackSent = false
                actionDispatched = false
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                if (!gestureStarted &&
                    (abs(event.x - lastX) > touchSlop || abs(event.y - lastY) > touchSlop)
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
                    dispatchMovement(event.x, event.y)
                } else {
                    lastX = event.x
                    lastY = event.y
                }
                if (event.actionMasked == MotionEvent.ACTION_UP) {
                    resetGesture()
                }
            }

            MotionEvent.ACTION_CANCEL -> resetGesture()
        }
        return true
    }

    private fun dispatchMovement(x: Float, y: Float) {
        val update = directionTracker.addMovement(x - lastX, y - lastY)
        lastX = x
        lastY = y
        if (update == null) {
            return
        }

        if (update.directionChanged) {
            feedbackSent = false
        }

        if (!isRepeatEnabled()) {
            if (update.steps > 0 && !actionDispatched) {
                onGesture(update.direction, 1, true)
                actionDispatched = true
            }
            return
        }

        if (update.steps > 0) {
            onGesture(update.direction, update.steps, !feedbackSent)
            feedbackSent = true
        }
    }

    private fun resetGesture() {
        gestureStarted = false
        directionTracker.reset()
        feedbackSent = false
        actionDispatched = false
    }
}
