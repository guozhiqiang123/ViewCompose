package com.gzq.uiframework.renderer.modifier

class Modifier private constructor(
    internal val elements: List<ModifierElement>,
) {
    fun then(element: ModifierElement): Modifier = Modifier(elements + element)

    companion object {
        val Empty: Modifier = Modifier(emptyList())
    }
}

interface ModifierElement

data class PaddingModifierElement(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
) : ModifierElement

data class BackgroundColorModifierElement(
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

enum class Visibility {
    Visible,
    Invisible,
    Gone,
}

fun Modifier.padding(all: Int): Modifier {
    return then(
        PaddingModifierElement(
            left = all,
            top = all,
            right = all,
            bottom = all,
        ),
    )
}

fun Modifier.backgroundColor(color: Int): Modifier {
    return then(
        BackgroundColorModifierElement(color),
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

fun Modifier.weight(weight: Float): Modifier {
    require(weight > 0f) {
        "weight must be > 0"
    }
    return then(
        WeightModifierElement(weight),
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
