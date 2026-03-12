package com.viewcompose.ui.modifier

open class Modifier private constructor(
    val elements: List<ModifierElement>,
) {
    fun then(element: ModifierElement): Modifier = Modifier(elements + element)

    fun then(modifier: Modifier): Modifier = Modifier(elements + modifier.elements)

    companion object : Modifier(emptyList())
}

interface ModifierElement
