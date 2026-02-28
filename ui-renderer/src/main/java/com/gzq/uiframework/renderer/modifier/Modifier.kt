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
