package com.gzq.uiframework.widget.core

object ButtonDefaults {
    fun containerColor(): Int = Theme.colors.primary

    fun contentColor(): Int = contentColorFor(containerColor())

    fun cornerRadius(): Int = Theme.shapes.controlCornerRadius

    fun pressedColor(): Int = Theme.interactions.pressedOverlay
}

internal fun contentColorFor(backgroundColor: Int): Int {
    val red = backgroundColor shr 16 and 0xFF
    val green = backgroundColor shr 8 and 0xFF
    val blue = backgroundColor and 0xFF
    val luma = 0.299 * red + 0.587 * green + 0.114 * blue
    return if (luma >= 186) {
        0xFF000000.toInt()
    } else {
        0xFFFFFFFF.toInt()
    }
}
