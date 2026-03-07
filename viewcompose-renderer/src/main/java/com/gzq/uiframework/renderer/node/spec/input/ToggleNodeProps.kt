package com.gzq.uiframework.renderer.node.spec

data class ToggleNodeProps(
    val text: CharSequence?,
    val enabled: Boolean,
    val checked: Boolean,
    val controlColor: Int,
    val thumbColor: Int? = null,
    val trackColor: Int? = null,
    val checkedColor: Int? = null,
    val uncheckedColor: Int? = null,
    val onCheckedChange: ((Boolean) -> Unit)?,
    val textColor: Int,
    val textSizeSp: Int,
    val rippleColor: Int,
) : NodeSpec
