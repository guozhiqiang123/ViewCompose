package com.viewcompose.ui.modifier

import com.viewcompose.ui.layout.BoxAlignment
import com.viewcompose.ui.gesture.GestureOrientation
import com.viewcompose.ui.gesture.GesturePriority
import com.viewcompose.ui.gesture.PointerEvent
import com.viewcompose.ui.gesture.PointerEventResult
import com.viewcompose.ui.gesture.SwipeDirection
import com.viewcompose.ui.gesture.TransformDelta
import com.viewcompose.ui.layout.HorizontalAlignment
import com.viewcompose.ui.layout.VerticalAlignment

open class Modifier private constructor(
    val elements: List<ModifierElement>,
) {
    fun then(element: ModifierElement): Modifier = Modifier(elements + element)

    fun then(modifier: Modifier): Modifier = Modifier(elements + modifier.elements)

    companion object : Modifier(emptyList())
}

interface ModifierElement

data class PaddingModifierElement(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) : ModifierElement

data class SystemBarsInsetsPaddingModifierElement(
    val left: Boolean,
    val top: Boolean,
    val right: Boolean,
    val bottom: Boolean,
) : ModifierElement

data class ImeInsetsPaddingModifierElement(
    val left: Boolean,
    val top: Boolean,
    val right: Boolean,
    val bottom: Boolean,
) : ModifierElement

data class MarginModifierElement(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) : ModifierElement

data class BackgroundColorModifierElement(
    val color: Int,
) : ModifierElement

data class BackgroundDrawableResModifierElement(
    val resId: Int,
) : ModifierElement

data class BorderModifierElement(
    val width: Int,
    val color: Int,
) : ModifierElement

data class CornerRadiusModifierElement(
    val topStart: Int,
    val topEnd: Int,
    val bottomEnd: Int,
    val bottomStart: Int,
) : ModifierElement {
    val isUniform: Boolean
        get() = topStart == topEnd && topEnd == bottomEnd && bottomEnd == bottomStart
}

data class ClipModifierElement(
    val clip: Boolean = true,
) : ModifierElement

data class ElevationModifierElement(
    val elevation: Int,
) : ModifierElement

data class SizeModifierElement(
    val width: Int,
    val height: Int,
) : ModifierElement

data class WidthModifierElement(
    val width: Int,
) : ModifierElement

data class HeightModifierElement(
    val height: Int,
) : ModifierElement

data class MinHeightModifierElement(
    val minHeight: Int,
) : ModifierElement

data class MinWidthModifierElement(
    val minWidth: Int,
) : ModifierElement

data class AlphaModifierElement(
    val alpha: Float,
) : ModifierElement

data class VisibilityModifierElement(
    val visibility: Visibility,
) : ModifierElement

data class ClickableModifierElement(
    val onClick: () -> Unit,
) : ModifierElement

data class PointerInputModifierElement(
    val key: Any,
    val onEvent: (PointerEvent) -> PointerEventResult,
) : ModifierElement

data class CombinedClickableModifierElement(
    val enabled: Boolean,
    val onClick: (() -> Unit)?,
    val onDoubleClick: (() -> Unit)?,
    val onLongClick: (() -> Unit)?,
) : ModifierElement

data class DraggableModifierElement(
    val enabled: Boolean,
    val orientation: GestureOrientation,
    val onDragStarted: (() -> Unit)?,
    val onDragStopped: ((velocity: Float) -> Unit)?,
    val onDelta: (delta: Float) -> Unit,
) : ModifierElement

data class SwipeableModifierElement(
    val enabled: Boolean,
    val orientation: GestureOrientation,
    val onSwipe: (SwipeDirection) -> Unit,
    val onDelta: ((delta: Float) -> Unit)? = null,
) : ModifierElement

data class TransformableModifierElement(
    val enabled: Boolean,
    val onTransform: (TransformDelta) -> Unit,
) : ModifierElement

data class GesturePriorityModifierElement(
    val priority: GesturePriority,
) : ModifierElement

data class ContentDescriptionModifierElement(
    val contentDescription: String?,
) : ModifierElement

data class TestTagModifierElement(
    val tag: String,
) : ModifierElement

data class OverlayAnchorModifierElement(
    val anchorId: String,
) : ModifierElement

data class WeightModifierElement(
    val weight: Float,
) : ModifierElement

data class BoxAlignModifierElement(
    val alignment: BoxAlignment,
) : ModifierElement

data class HorizontalAlignModifierElement(
    val alignment: HorizontalAlignment,
) : ModifierElement

data class VerticalAlignModifierElement(
    val alignment: VerticalAlignment,
) : ModifierElement

data class OffsetModifierElement(
    val x: Float,
    val y: Float,
) : ModifierElement

data class ZIndexModifierElement(
    val zIndex: Float,
) : ModifierElement

data class TransformOrigin(
    val pivotFractionX: Float,
    val pivotFractionY: Float,
) {
    companion object {
        val Center = TransformOrigin(
            pivotFractionX = 0.5f,
            pivotFractionY = 0.5f,
        )
    }
}

data class GraphicsLayerModifierElement(
    val scaleX: Float? = null,
    val scaleY: Float? = null,
    val rotationZ: Float? = null,
    val rotationX: Float? = null,
    val rotationY: Float? = null,
    val translationX: Float? = null,
    val translationY: Float? = null,
    val alpha: Float? = null,
    val transformOrigin: TransformOrigin? = null,
    val clip: Boolean? = null,
) : ModifierElement

data class LazyContainerReuseModifierElement(
    val sharePool: Boolean,
    val disableItemAnimator: Boolean,
) : ModifierElement

data class LazyContainerMotionModifierElement(
    val animateInsert: Boolean,
    val animateRemove: Boolean,
    val animateMove: Boolean,
    val animateChange: Boolean,
) : ModifierElement

data class FocusFollowKeyboardModifierElement(
    val enabled: Boolean,
) : ModifierElement

class NativeViewElement(
    val stableKey: Any,
    val configure: (Any) -> Unit,
) : ModifierElement {
    override fun equals(other: Any?): Boolean =
        other is NativeViewElement && stableKey == other.stableKey

    override fun hashCode(): Int = stableKey.hashCode()
}

enum class Visibility {
    Visible,
    Invisible,
    Gone,
}

fun Modifier.padding(all: Int): Modifier {
    return padding(
        horizontal = all,
        vertical = all,
    )
}

fun Modifier.padding(
    horizontal: Int = 0,
    vertical: Int = 0,
): Modifier {
    return padding(
        left = horizontal,
        top = vertical,
        right = horizontal,
        bottom = vertical,
    )
}

fun Modifier.padding(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
): Modifier {
    return then(
        PaddingModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        ),
    )
}

fun Modifier.systemBarsInsetsPadding(
    left: Boolean = true,
    top: Boolean = true,
    right: Boolean = true,
    bottom: Boolean = true,
): Modifier {
    return then(
        SystemBarsInsetsPaddingModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        ),
    )
}

fun Modifier.imeInsetsPadding(
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = true,
): Modifier {
    return then(
        ImeInsetsPaddingModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        ),
    )
}

fun Modifier.backgroundColor(color: Int): Modifier {
    return then(
        BackgroundColorModifierElement(color),
    )
}

fun Modifier.backgroundDrawableRes(resId: Int): Modifier {
    return then(
        BackgroundDrawableResModifierElement(resId),
    )
}

fun Modifier.border(
    width: Int,
    color: Int,
): Modifier {
    return then(
        BorderModifierElement(
            width = width,
            color = color,
        ),
    )
}

fun Modifier.cornerRadius(radius: Int): Modifier {
    return cornerRadius(top = radius, bottom = radius)
}

fun Modifier.cornerRadius(
    top: Int = 0,
    bottom: Int = 0,
): Modifier {
    return cornerRadius(
        topStart = top,
        topEnd = top,
        bottomEnd = bottom,
        bottomStart = bottom,
    )
}

fun Modifier.cornerRadius(
    topStart: Int = 0,
    topEnd: Int = 0,
    bottomEnd: Int = 0,
    bottomStart: Int = 0,
): Modifier {
    return then(
        CornerRadiusModifierElement(
            topStart = topStart,
            topEnd = topEnd,
            bottomEnd = bottomEnd,
            bottomStart = bottomStart,
        ),
    )
}

fun Modifier.clip(): Modifier {
    return then(
        ClipModifierElement(clip = true),
    )
}

fun Modifier.elevation(elevation: Int): Modifier {
    return then(
        ElevationModifierElement(elevation),
    )
}

fun Modifier.margin(all: Int): Modifier {
    return margin(
        horizontal = all,
        vertical = all,
    )
}

fun Modifier.margin(
    horizontal: Int = 0,
    vertical: Int = 0,
): Modifier {
    return margin(
        left = horizontal,
        top = vertical,
        right = horizontal,
        bottom = vertical,
    )
}

fun Modifier.margin(
    left: Int = 0,
    top: Int = 0,
    right: Int = 0,
    bottom: Int = 0,
): Modifier {
    return then(
        MarginModifierElement(
            left = left,
            top = top,
            right = right,
            bottom = bottom,
        ),
    )
}

fun Modifier.size(
    width: Int,
    height: Int,
): Modifier {
    return then(
        SizeModifierElement(
            width = width,
            height = height,
        ),
    )
}

fun Modifier.width(width: Int): Modifier {
    return then(
        WidthModifierElement(width),
    )
}

fun Modifier.height(height: Int): Modifier {
    return then(
        HeightModifierElement(height),
    )
}

fun Modifier.minHeight(minHeight: Int): Modifier {
    return then(
        MinHeightModifierElement(minHeight),
    )
}

fun Modifier.minWidth(minWidth: Int): Modifier {
    return then(
        MinWidthModifierElement(minWidth),
    )
}

fun Modifier.alpha(alpha: Float): Modifier {
    return then(
        AlphaModifierElement(alpha),
    )
}

fun Modifier.visibility(visibility: Visibility): Modifier {
    return then(
        VisibilityModifierElement(visibility),
    )
}

fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return then(
        ClickableModifierElement(onClick),
    )
}

fun Modifier.contentDescription(description: String?): Modifier {
    return then(
        ContentDescriptionModifierElement(description),
    )
}

fun Modifier.testTag(tag: String): Modifier {
    return then(
        TestTagModifierElement(tag),
    )
}

fun Modifier.overlayAnchor(anchorId: String): Modifier {
    return then(
        OverlayAnchorModifierElement(anchorId),
    )
}

fun Modifier.offset(
    x: Float = 0f,
    y: Float = 0f,
): Modifier {
    return then(
        OffsetModifierElement(
            x = x,
            y = y,
        ),
    )
}

fun Modifier.zIndex(zIndex: Float): Modifier {
    return then(
        ZIndexModifierElement(zIndex),
    )
}

fun Modifier.graphicsLayer(
    scaleX: Float? = null,
    scaleY: Float? = null,
    rotationZ: Float? = null,
    rotationX: Float? = null,
    rotationY: Float? = null,
    translationX: Float? = null,
    translationY: Float? = null,
    alpha: Float? = null,
    transformOrigin: TransformOrigin? = null,
    clip: Boolean? = null,
): Modifier {
    return then(
        GraphicsLayerModifierElement(
            scaleX = scaleX,
            scaleY = scaleY,
            rotationZ = rotationZ,
            rotationX = rotationX,
            rotationY = rotationY,
            translationX = translationX,
            translationY = translationY,
            alpha = alpha,
            transformOrigin = transformOrigin,
            clip = clip,
        ),
    )
}

fun Modifier.lazyContainerReuse(
    sharePool: Boolean = false,
    disableItemAnimator: Boolean = false,
): Modifier {
    return then(
        LazyContainerReuseModifierElement(
            sharePool = sharePool,
            disableItemAnimator = disableItemAnimator,
        ),
    )
}

fun Modifier.lazyContainerMotion(
    animateInsert: Boolean = true,
    animateRemove: Boolean = true,
    animateMove: Boolean = true,
    animateChange: Boolean = true,
): Modifier {
    return then(
        LazyContainerMotionModifierElement(
            animateInsert = animateInsert,
            animateRemove = animateRemove,
            animateMove = animateMove,
            animateChange = animateChange,
        ),
    )
}

fun Modifier.focusFollowKeyboard(
    enabled: Boolean = true,
): Modifier {
    return then(
        FocusFollowKeyboardModifierElement(
            enabled = enabled,
        ),
    )
}

fun Modifier.fillMaxWidth(): Modifier {
    return width(MATCH_PARENT)
}

fun Modifier.fillMaxHeight(): Modifier {
    return height(MATCH_PARENT)
}

fun Modifier.fillMaxSize(): Modifier {
    return size(
        width = MATCH_PARENT,
        height = MATCH_PARENT,
    )
}

private const val MATCH_PARENT: Int = -1
