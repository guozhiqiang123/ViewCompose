package com.viewcompose.ui.node.spec

import com.viewcompose.ui.node.ImageSource

data class ButtonNodeProps(
    val text: CharSequence?,
    val enabled: Boolean,
    val onClick: (() -> Unit)?,
    val textColor: Int,
    val textSizeSp: Int,
    val backgroundColor: Int,
    val borderWidth: Int,
    val borderColor: Int,
    val cornerRadius: Int,
    val rippleColor: Int,
    val minHeight: Int,
    val paddingHorizontal: Int,
    val paddingVertical: Int,
    val leadingIcon: ImageSource.Resource?,
    val trailingIcon: ImageSource.Resource?,
    val iconTint: Int,
    val iconSize: Int,
    val iconSpacing: Int,
) : NodeSpec
