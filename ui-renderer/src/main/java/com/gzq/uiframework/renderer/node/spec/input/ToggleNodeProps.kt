package com.gzq.uiframework.renderer.node.spec

data class ToggleNodeProps(
    val text: CharSequence?,
    val enabled: Boolean,
    val checked: Boolean,
    val controlColor: Int,
    val onCheckedChange: ((Boolean) -> Unit)?,
) : NodeSpec
