package com.gzq.uiframework.renderer.modifier

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.VerticalAlignment

open class Modifier private constructor(
    internal val elements: List<ModifierElement>,
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

data class MarginModifierElement(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) : ModifierElement

data class BackgroundColorModifierElement(
    val color: Int,
) : ModifierElement

data class BorderModifierElement(
    val width: Int,
    val color: Int,
) : ModifierElement

data class CornerRadiusModifierElement(
    val radius: Int,
) : ModifierElement

data class RippleColorModifierElement(
    val color: Int,
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

data class AlphaModifierElement(
    val alpha: Float,
) : ModifierElement

data class VisibilityModifierElement(
    val visibility: Visibility,
) : ModifierElement

data class ClickableModifierElement(
    val onClick: () -> Unit,
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

class NativeViewElement(
    val stableKey: Any,
    val configure: (android.view.View) -> Unit,
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

fun Modifier.backgroundColor(color: Int): Modifier {
    return then(
        BackgroundColorModifierElement(color),
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
    return then(
        CornerRadiusModifierElement(radius),
    )
}

fun Modifier.rippleColor(color: Int): Modifier {
    return then(
        RippleColorModifierElement(color),
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

@Deprecated(
    message = "weight is parent-data. Prefer RowScope.weight(...) or ColumnScope.weight(...).",
)
fun Modifier.weight(weight: Float): Modifier {
    require(weight > 0f) {
        "weight must be > 0"
    }
    return then(
        WeightModifierElement(weight),
    )
}

@Deprecated(
    message = "Box alignment is parent-data. Prefer BoxScope.align(...).",
)
fun Modifier.align(alignment: BoxAlignment): Modifier {
    return then(
        BoxAlignModifierElement(alignment),
    )
}

@Deprecated(
    message = "Horizontal alignment is parent-data. Prefer ColumnScope.align(...).",
)
fun Modifier.align(alignment: HorizontalAlignment): Modifier {
    return then(
        HorizontalAlignModifierElement(alignment),
    )
}

@Deprecated(
    message = "Vertical alignment is parent-data. Prefer RowScope.align(...).",
)
fun Modifier.align(alignment: VerticalAlignment): Modifier {
    return then(
        VerticalAlignModifierElement(alignment),
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

fun Modifier.fillMaxWidth(): Modifier {
    return width(android.view.ViewGroup.LayoutParams.MATCH_PARENT)
}

fun Modifier.fillMaxHeight(): Modifier {
    return height(android.view.ViewGroup.LayoutParams.MATCH_PARENT)
}

fun Modifier.fillMaxSize(): Modifier {
    return size(
        width = android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        height = android.view.ViewGroup.LayoutParams.MATCH_PARENT,
    )
}

fun Modifier.nativeView(key: Any = Unit, configure: (android.view.View) -> Unit): Modifier {
    return then(NativeViewElement(key, configure))
}
