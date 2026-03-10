package com.viewcompose

import android.view.Choreographer
import com.viewcompose.gesture.combinedClickable
import com.viewcompose.gesture.draggable
import com.viewcompose.gesture.gesturePriority
import com.viewcompose.gesture.pointerInput
import com.viewcompose.gesture.rememberDraggableState
import com.viewcompose.gesture.rememberSwipeableState
import com.viewcompose.gesture.rememberTransformableState
import com.viewcompose.gesture.swipeable
import com.viewcompose.gesture.transformable
import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.GesturePriority
import com.viewcompose.ui.gesture.PointerEventResult
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.fillMaxSize
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.graphicsLayer
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.modifier.padding
import com.viewcompose.ui.modifier.testTag
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.Button
import com.viewcompose.widget.core.Column
import com.viewcompose.widget.core.DisposableEffect
import com.viewcompose.widget.core.LazyColumn
import com.viewcompose.widget.core.Surface
import com.viewcompose.widget.core.SurfaceVariant
import com.viewcompose.widget.core.Text
import com.viewcompose.widget.core.TextDefaults
import com.viewcompose.widget.core.UiTextStyle
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.dp
import com.viewcompose.widget.core.remember
import com.viewcompose.widget.core.sp
import kotlin.math.roundToInt

internal fun UiTreeBuilder.GesturePage(
    initialPageIndex: Int = 0,
) {
    val selectedPageState = remember { mutableStateOf(initialPageIndex.coerceIn(0, 2)) }
    val tapCountState = remember { mutableStateOf(0) }
    val dragOffsetState = remember { mutableStateOf(0f) }
    val dragTextOffsetState = remember { mutableStateOf(0f) }
    val dragTextFrameUpdater = remember {
        FrameCoalescedFloatUpdater { value ->
            dragTextOffsetState.value = value
        }
    }
    val swipeState = rememberSwipeableState("Left")
    val pointerEventState = remember { mutableStateOf("None") }
    val scaleState = remember { mutableStateOf(1f) }
    val rotationState = remember { mutableStateOf(0f) }
    val panXState = remember { mutableStateOf(0f) }
    val panYState = remember { mutableStateOf(0f) }
    val transformLogState = remember { mutableStateOf("idle") }
    DisposableEffect(dragTextFrameUpdater) {
        return@DisposableEffect {
            dragTextFrameUpdater.dispose()
        }
    }

    val draggableState = rememberDraggableState { delta ->
        val nextOffset = (dragOffsetState.value + delta).coerceIn(-240f, 240f)
        dragOffsetState.value = nextOffset
        dragTextFrameUpdater.submit(nextOffset)
    }
    val transformState = rememberTransformableState { zoom, panX, panY, rotation ->
        scaleState.value = (scaleState.value * zoom).coerceIn(0.6f, 2.2f)
        panXState.value = (panXState.value + panX).coerceIn(-120f, 120f)
        panYState.value = (panYState.value + panY).coerceIn(-120f, 120f)
        rotationState.value += rotation
        transformLogState.value = "zoom=${"%.2f".format(scaleState.value)} rot=${"%.1f".format(rotationState.value)}"
    }

    val sections = when (selectedPageState.value) {
        0 -> listOf("page", "filter", "tap", "verify")
        1 -> listOf("page", "filter", "drag_swipe", "verify")
        else -> listOf("page", "filter", "transform", "verify")
    }

    LazyColumn(
        items = sections,
        key = { it },
        modifier = Modifier.fillMaxSize(),
    ) { section ->
        when (section) {
            "page" -> ChapterPageOverviewSection(
                title = "Gestures",
                goal = "验证 pointer/tap/drag/swipe/transform 在 renderer 分发链路中可用，并与 clickable 回落策略兼容。",
                modules = listOf("viewcompose-gesture", "renderer gesture dispatcher", "nested conflict policy"),
            )

            "filter" -> ChapterPageFilterSection(
                pages = listOf("Tap", "Drag+Swipe", "Transform"),
                selectedIndex = selectedPageState.value,
                onSelectionChange = { selectedPageState.value = it },
            )

            "tap" -> ScenarioSection(
                kind = ScenarioKind.Core,
                title = "Tap / Double / Long",
                subtitle = "combinedClickable + pointerInput 组合，验证点击族事件与 pointer 事件日志。",
            ) {
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { tapCountState.value += 1 },
                            onDoubleClick = { tapCountState.value += 2 },
                            onLongClick = { tapCountState.value += 10 },
                        )
                        .pointerInput(key = "tap-pointer") { event ->
                            pointerEventState.value = event.type.name
                            PointerEventResult.Ignored
                        }
                        .padding(14.dp)
                        .testTag(DemoTestTags.GESTURE_TAP_TARGET),
                ) {
                    Text(text = "Tap target (single + double + long)")
                }
                Text(
                    text = "Tap count: ${tapCountState.value}",
                    modifier = Modifier.testTag(DemoTestTags.GESTURE_TAP_COUNT),
                )
                Text(
                    text = "Pointer: ${pointerEventState.value}",
                    color = TextDefaults.secondaryColor(),
                    modifier = Modifier.testTag(DemoTestTags.GESTURE_POINTER_LOG),
                )
            }

            "drag_swipe" -> ScenarioSection(
                kind = ScenarioKind.Stress,
                title = "Drag / Swipe",
                subtitle = "draggable 与 swipeable 在同一页共存，含方向锁、slop 与优先级策略。",
            ) {
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .gesturePriority(GesturePriority.High)
                        .draggable(
                            state = draggableState,
                            orientation = GestureOrientation.Horizontal,
                        )
                        .graphicsLayer(translationX = dragOffsetState.value)
                        .padding(12.dp)
                        .testTag(DemoTestTags.GESTURE_DRAG_TARGET),
                ) {
                    Text(text = "Drag horizontally")
                }
                Text(
                    text = "Drag x = ${dragTextOffsetState.value.roundToInt()}",
                    modifier = Modifier
                        .margin(top = 6.dp)
                        .testTag(DemoTestTags.GESTURE_DRAG_VALUE),
                )
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .margin(top = 10.dp)
                        .swipeable(
                            state = swipeState,
                            anchors = mapOf(0f to "Left", 120f to "Right"),
                            orientation = GestureOrientation.Horizontal,
                        )
                        .padding(12.dp)
                        .testTag(DemoTestTags.GESTURE_SWIPE_TARGET),
                ) {
                    Text(text = "Swipe horizontally")
                }
                Text(
                    text = "Swipe state = ${swipeState.currentValue.value}",
                    modifier = Modifier
                        .margin(top = 6.dp)
                        .testTag(DemoTestTags.GESTURE_SWIPE_VALUE),
                )
            }

            "transform" -> ScenarioSection(
                kind = ScenarioKind.Visual,
                title = "Transform",
                subtitle = "transformable + pointerInput 验证缩放、平移、旋转事件在同一目标上更新。",
            ) {
                Surface(
                    variant = SurfaceVariant.Variant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .transformable(state = transformState)
                        .pointerInput(key = "transform-pointer") { event ->
                            pointerEventState.value = event.type.name
                            PointerEventResult.Ignored
                        }
                        .graphicsLayer(
                            scaleX = scaleState.value,
                            scaleY = scaleState.value,
                            translationX = panXState.value,
                            translationY = panYState.value,
                            rotationZ = rotationState.value,
                        )
                        .padding(14.dp)
                        .testTag(DemoTestTags.GESTURE_TRANSFORM_TARGET),
                ) {
                    Text(text = "Pinch / rotate / pan target")
                }
                Text(
                    text = transformLogState.value,
                    style = UiTextStyle(fontSizeSp = 13.sp),
                    modifier = Modifier
                        .margin(top = 8.dp)
                        .testTag(DemoTestTags.GESTURE_TRANSFORM_VALUE),
                )
                Text(
                    text = "Pointer: ${pointerEventState.value}",
                    color = TextDefaults.secondaryColor(),
                )
            }

            else -> VerificationNotesSection(
                what = "Gestures 页覆盖 tap、drag/swipe、transform 与 pointer 事件链路。",
                howToVerify = listOf(
                    "Tap 页点击目标区域，观察计数增长与 pointer 日志变化。",
                    "Drag+Swipe 页横向拖拽与滑动，确认 drag 值和 swipe 状态更新。",
                    "Transform 页做双指缩放/平移/旋转，确认日志数值持续变化。",
                ),
                expected = listOf(
                    "手势消费成功时不会回落触发 clickable。",
                    "方向锁与 slop 策略避免误触父滚动容器。",
                    "pointerInput 可以并行记录事件而不破坏高层手势。",
                ),
            )
        }
    }
}

private class FrameCoalescedFloatUpdater(
    private val onValue: (Float) -> Unit,
) : Choreographer.FrameCallback {
    private var scheduled = false
    private var hasPending = false
    private var pendingValue = 0f

    fun submit(value: Float) {
        pendingValue = value
        hasPending = true
        if (scheduled) {
            return
        }
        scheduled = true
        Choreographer.getInstance().postFrameCallback(this)
    }

    override fun doFrame(frameTimeNanos: Long) {
        scheduled = false
        if (!hasPending) {
            return
        }
        hasPending = false
        onValue(pendingValue)
    }

    fun dispose() {
        if (scheduled) {
            Choreographer.getInstance().removeFrameCallback(this)
        }
        scheduled = false
        hasPending = false
    }
}
