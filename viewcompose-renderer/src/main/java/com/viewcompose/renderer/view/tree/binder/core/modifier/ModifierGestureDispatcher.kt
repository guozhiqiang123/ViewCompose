package com.viewcompose.renderer.view.tree

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import com.viewcompose.renderer.R
import com.viewcompose.renderer.modifier.ResolvedModifiers
import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.GesturePriority
import com.viewcompose.ui.gesture.PointerChange
import com.viewcompose.ui.gesture.PointerEvent
import com.viewcompose.ui.gesture.PointerEventResult
import com.viewcompose.ui.gesture.PointerEventType
import com.viewcompose.ui.gesture.SwipeDirection
import com.viewcompose.ui.gesture.TransformDelta
import kotlin.math.abs
import kotlin.math.atan2

internal object ModifierGestureApplier {
    fun applyGestureState(
        view: View,
        resolved: ResolvedModifiers,
    ) {
        val hasGesture = resolved.pointerInput != null ||
            resolved.combinedClickable != null ||
            resolved.draggable != null ||
            resolved.swipeable != null ||
            resolved.transformable != null
        if (!hasGesture) {
            (view.getTag(R.id.ui_framework_gesture_dispatcher) as? ViewGestureDispatcher)?.dispose()
            view.setTag(R.id.ui_framework_gesture_dispatcher, null)
            view.setOnTouchListener(null)
            return
        }
        val dispatcher = (view.getTag(R.id.ui_framework_gesture_dispatcher) as? ViewGestureDispatcher)
            ?: ViewGestureDispatcher(view).also {
                view.setTag(R.id.ui_framework_gesture_dispatcher, it)
            }
        dispatcher.update(resolved)
        view.setOnTouchListener(dispatcher)
    }
}

private class ViewGestureDispatcher(
    private val hostView: View,
) : View.OnTouchListener {
    private enum class Axis {
        Horizontal,
        Vertical,
    }

    private var resolved: ResolvedModifiers = ResolvedModifiers()
    private val touchSlop: Float = ViewConfiguration.get(hostView.context).scaledTouchSlop.toFloat()
    private val swipeTriggerDistance: Float
        get() = touchSlop * 4f

    private var velocityTracker: VelocityTracker? = null
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lockAxis: Axis? = null
    private var dragStarted = false
    private var combinedTapConsumed = false
    private var transformMidX = Float.NaN
    private var transformMidY = Float.NaN
    private var transformAngle = Float.NaN

    private val combinedDetector = GestureDetector(
        hostView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                return resolved.combinedClickable?.enabled == true
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val element = resolved.combinedClickable ?: return false
                val onClick = element.onClick ?: return false
                if (!element.enabled) return false
                onClick()
                combinedTapConsumed = true
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val element = resolved.combinedClickable ?: return false
                val onDoubleClick = element.onDoubleClick ?: return false
                if (!element.enabled) return false
                onDoubleClick()
                combinedTapConsumed = true
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val element = resolved.combinedClickable ?: return
                val onLongClick = element.onLongClick ?: return
                if (!element.enabled) return
                onLongClick()
                combinedTapConsumed = true
            }
        },
    )

    private val scaleDetector = ScaleGestureDetector(
        hostView.context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val transform = resolved.transformable ?: return false
                if (!transform.enabled) return false
                transform.onTransform(
                    TransformDelta(
                        zoom = detector.scaleFactor,
                    ),
                )
                return true
            }
        },
    )

    fun update(resolved: ResolvedModifiers) {
        this.resolved = resolved
    }

    fun dispose() {
        velocityTracker?.recycle()
        velocityTracker = null
    }

    override fun onTouch(
        v: View,
        event: MotionEvent,
    ): Boolean {
        val combinedEnabled = resolved.combinedClickable?.enabled == true
        val requiresContinuousStream =
            resolved.draggable?.enabled == true ||
                resolved.swipeable?.enabled == true ||
                resolved.transformable?.enabled == true
        if (combinedEnabled) {
            combinedDetector.onTouchEvent(event)
        }
        val pointerConsumed = dispatchPointerInput(event)
        val transformConsumed = dispatchTransform(event)
        val dragSwipeConsumed = dispatchDragAndSwipe(event)
        if (event.actionMasked == MotionEvent.ACTION_DOWN &&
            resolved.gesturePriority?.priority == GesturePriority.High
        ) {
            hostView.parent?.requestDisallowInterceptTouchEvent(true)
        }
        val consumed = pointerConsumed || transformConsumed || dragSwipeConsumed || combinedTapConsumed
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            combinedTapConsumed = false
        }
        // combinedClickable owns tap gesture arbitration; keep fallback clickable disabled for this pointer stream.
        if (combinedEnabled) {
            if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
                resetTrackingState()
            }
            return true
        }
        if (event.actionMasked == MotionEvent.ACTION_DOWN && requiresContinuousStream) {
            // Keep the pointer stream on this view so drag/swipe/transform can receive MOVE/UP events.
            return true
        }
        return consumed
    }

    private fun dispatchPointerInput(event: MotionEvent): Boolean {
        val pointer = resolved.pointerInput ?: return false
        val result = pointer.onEvent(
            event.toPointerEvent(),
        )
        return result == PointerEventResult.Consumed
    }

    private fun dispatchTransform(event: MotionEvent): Boolean {
        val transform = resolved.transformable ?: return false
        if (!transform.enabled) return false
        val scaleConsumed = scaleDetector.onTouchEvent(event)
        var moved = false
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount >= 2) {
                    transformMidX = (event.getX(0) + event.getX(1)) * 0.5f
                    transformMidY = (event.getY(0) + event.getY(1)) * 0.5f
                    transformAngle = calculateAngle(event)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    val nextMidX = (event.getX(0) + event.getX(1)) * 0.5f
                    val nextMidY = (event.getY(0) + event.getY(1)) * 0.5f
                    val nextAngle = calculateAngle(event)
                    val panX = if (transformMidX.isNaN()) 0f else nextMidX - transformMidX
                    val panY = if (transformMidY.isNaN()) 0f else nextMidY - transformMidY
                    val rotation = if (transformAngle.isNaN()) 0f else normalizeAngle(nextAngle - transformAngle)
                    if (abs(panX) > 0.5f || abs(panY) > 0.5f || abs(rotation) > 0.5f) {
                        transform.onTransform(
                            TransformDelta(
                                panX = panX,
                                panY = panY,
                                rotation = rotation,
                            ),
                        )
                        moved = true
                    }
                    transformMidX = nextMidX
                    transformMidY = nextMidY
                    transformAngle = nextAngle
                }
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (event.pointerCount <= 2) {
                    transformMidX = Float.NaN
                    transformMidY = Float.NaN
                    transformAngle = Float.NaN
                }
            }
        }
        return scaleConsumed || moved
    }

    private fun dispatchDragAndSwipe(event: MotionEvent): Boolean {
        val draggable = resolved.draggable?.takeIf { it.enabled }
        val swipeable = resolved.swipeable?.takeIf { it.enabled }
        if (draggable == null && swipeable == null) {
            resetTrackingState()
            return false
        }
        velocityTracker = (velocityTracker ?: VelocityTracker.obtain()).also { tracker ->
            tracker.addMovement(event)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.rawX
                downY = event.rawY
                lastX = downX
                lastY = downY
                lockAxis = null
                dragStarted = false
                velocityTracker?.clear()
                velocityTracker?.addMovement(event)
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                val rawX = event.rawX
                val rawY = event.rawY
                val dx = rawX - downX
                val dy = rawY - downY
                if (lockAxis == null) {
                    lockAxis = resolveLockAxis(
                        dx = dx,
                        dy = dy,
                        orientation = resolveGestureOrientation(draggable, swipeable),
                    )
                    if (lockAxis != null) {
                        hostView.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                val axis = lockAxis ?: return false
                val delta = if (axis == Axis.Horizontal) {
                    rawX - lastX
                } else {
                    rawY - lastY
                }
                lastX = rawX
                lastY = rawY
                if (abs(delta) <= 0f) {
                    return false
                }
                if (!dragStarted) {
                    dragStarted = true
                    draggable?.onDragStarted?.invoke()
                }
                draggable?.onDelta?.invoke(delta)
                swipeable?.onDelta?.invoke(delta)
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val rawX = event.rawX
                val rawY = event.rawY
                val axis = lockAxis
                val total = when (axis) {
                    Axis.Horizontal -> rawX - downX
                    Axis.Vertical -> rawY - downY
                    null -> 0f
                }
                if (dragStarted) {
                    velocityTracker?.computeCurrentVelocity(1000)
                    val velocity = when (axis) {
                        Axis.Horizontal -> velocityTracker?.xVelocity ?: 0f
                        Axis.Vertical -> velocityTracker?.yVelocity ?: 0f
                        null -> 0f
                    }
                    draggable?.onDragStopped?.invoke(velocity)
                }
                val swipeConsumed = if (axis != null && abs(total) >= swipeTriggerDistance) {
                    val direction = when (axis) {
                        Axis.Horizontal -> if (total >= 0f) {
                            SwipeDirection.StartToEnd
                        } else {
                            SwipeDirection.EndToStart
                        }

                        Axis.Vertical -> if (total >= 0f) {
                            SwipeDirection.TopToBottom
                        } else {
                            SwipeDirection.BottomToTop
                        }
                    }
                    swipeable?.onSwipe?.invoke(direction)
                    swipeable != null
                } else {
                    false
                }
                val dragConsumed = dragStarted
                resetTrackingState()
                return dragConsumed || swipeConsumed
            }
        }
        return false
    }

    private fun resetTrackingState() {
        velocityTracker?.recycle()
        velocityTracker = null
        lockAxis = null
        dragStarted = false
        transformMidX = Float.NaN
        transformMidY = Float.NaN
        transformAngle = Float.NaN
    }

    private fun resolveGestureOrientation(
        draggable: com.viewcompose.ui.modifier.DraggableModifierElement?,
        swipeable: com.viewcompose.ui.modifier.SwipeableModifierElement?,
    ): GestureOrientation {
        return draggable?.orientation ?: swipeable?.orientation ?: GestureOrientation.Free
    }

    private fun resolveLockAxis(
        dx: Float,
        dy: Float,
        orientation: GestureOrientation,
    ): Axis? {
        val absDx = abs(dx)
        val absDy = abs(dy)
        return when (orientation) {
            GestureOrientation.Horizontal -> {
                if (absDx < touchSlop || absDx < absDy) null else Axis.Horizontal
            }

            GestureOrientation.Vertical -> {
                if (absDy < touchSlop || absDy < absDx) null else Axis.Vertical
            }

            GestureOrientation.Free -> {
                if (maxOf(absDx, absDy) < touchSlop) {
                    null
                } else if (absDx >= absDy) {
                    Axis.Horizontal
                } else {
                    Axis.Vertical
                }
            }
        }
    }

    private fun MotionEvent.toPointerEvent(): PointerEvent {
        return PointerEvent(
            type = toPointerEventType(actionMasked),
            uptimeMillis = eventTime,
            changes = List(pointerCount) { index ->
                val pressed = when (actionMasked) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> false
                    MotionEvent.ACTION_POINTER_UP -> index != actionIndex
                    else -> true
                }
                PointerChange(
                    id = getPointerId(index).toLong(),
                    x = getX(index),
                    y = getY(index),
                    pressed = pressed,
                )
            },
        )
    }

    private fun toPointerEventType(actionMasked: Int): PointerEventType {
        return when (actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> PointerEventType.Down
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> PointerEventType.Up
            MotionEvent.ACTION_CANCEL -> PointerEventType.Cancel
            else -> PointerEventType.Move
        }
    }

    private fun calculateAngle(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        return Math.toDegrees(
            atan2(
                (event.getY(1) - event.getY(0)).toDouble(),
                (event.getX(1) - event.getX(0)).toDouble(),
            ),
        ).toFloat()
    }

    private fun normalizeAngle(delta: Float): Float {
        var normalized = delta
        while (normalized > 180f) {
            normalized -= 360f
        }
        while (normalized < -180f) {
            normalized += 360f
        }
        return normalized
    }
}
