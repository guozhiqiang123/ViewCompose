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
