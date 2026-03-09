package com.viewcompose.gesture

import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.GesturePriority
import com.viewcompose.ui.gesture.PointerEvent
import com.viewcompose.ui.gesture.PointerEventResult
import com.viewcompose.ui.gesture.PointerEventType
import com.viewcompose.ui.gesture.SwipeDirection
import com.viewcompose.ui.gesture.TransformDelta
import com.viewcompose.ui.modifier.CombinedClickableModifierElement
import com.viewcompose.ui.modifier.DraggableModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.PointerInputModifierElement
import com.viewcompose.ui.modifier.SwipeableModifierElement
import com.viewcompose.ui.modifier.TransformableModifierElement
import com.viewcompose.ui.modifier.GesturePriorityModifierElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GestureModifiersTest {
    @Test
    fun `pointerInput appends pointer element`() {
        val modifier = Modifier.pointerInput(key = "p") {
            PointerEventResult.Consumed
        }
        val element = modifier.elements.single() as PointerInputModifierElement
        val result = element.onEvent(
            PointerEvent(
                type = PointerEventType.Down,
                uptimeMillis = 1L,
                changes = emptyList(),
            ),
        )
        assertEquals("p", element.key)
        assertEquals(PointerEventResult.Consumed, result)
    }

    @Test
    fun `combinedClickable appends expected element`() {
        val modifier = Modifier.combinedClickable(
            enabled = true,
            onClick = {},
            onDoubleClick = {},
            onLongClick = {},
        )
        val element = modifier.elements.single() as CombinedClickableModifierElement
        assertTrue(element.enabled)
        assertTrue(element.onClick != null)
        assertTrue(element.onDoubleClick != null)
        assertTrue(element.onLongClick != null)
    }

    @Test
    fun `draggable appends draggable element and forwards delta to state`() {
        var total = 0f
        val state = DraggableState(
            onDeltaState = com.viewcompose.runtime.mutableStateOf<(Float) -> Unit>(
                { delta -> total += delta },
            ),
        )
        val modifier = Modifier.draggable(
            state = state,
            orientation = GestureOrientation.Horizontal,
        )
        val element = modifier.elements.single() as DraggableModifierElement
        element.onDelta(5f)
        assertEquals(5f, total)
    }

    @Test
    fun `swipeable updates state using anchors`() {
        val state = SwipeableState(initialValue = "A")
        val modifier = Modifier.swipeable(
            state = state,
            anchors = mapOf(0f to "A", 100f to "B"),
            orientation = GestureOrientation.Horizontal,
        )
        val element = modifier.elements.single() as SwipeableModifierElement
        element.onSwipe(SwipeDirection.StartToEnd)
        assertEquals("B", state.currentValue.value)
        element.onSwipe(SwipeDirection.EndToStart)
        assertEquals("A", state.currentValue.value)
    }

    @Test
    fun `transformable appends transform element and forwards delta`() {
        var last = TransformDelta()
        val state = TransformableState(
            onTransformState = com.viewcompose.runtime.mutableStateOf<(TransformDelta) -> Unit>(
                { delta -> last = delta },
            ),
        )
        val modifier = Modifier.transformable(state = state)
        val element = modifier.elements.single() as TransformableModifierElement
        element.onTransform(
            TransformDelta(
                panX = 2f,
                panY = 3f,
                zoom = 1.2f,
                rotation = 10f,
            ),
        )
        assertEquals(2f, last.panX)
        assertEquals(3f, last.panY)
        assertEquals(1.2f, last.zoom)
        assertEquals(10f, last.rotation)
    }

    @Test
    fun `gesturePriority appends expected priority element`() {
        val modifier = Modifier.gesturePriority(GesturePriority.High)
        val element = modifier.elements.single() as GesturePriorityModifierElement
        assertEquals(GesturePriority.High, element.priority)
    }
}
