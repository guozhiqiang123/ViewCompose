package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.ImageSource

data class ButtonNodeProps(
    val text: CharSequence?,
    val enabled: Boolean,
    val iconSpacing: Int,
    val leadingIcon: ImageSource.Resource?,
    val trailingIcon: ImageSource.Resource?,
    val iconTint: Int,
    val iconSize: Int,
    val onClick: (() -> Unit)?,
) : NodeSpec
