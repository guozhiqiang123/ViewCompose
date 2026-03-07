package com.viewcompose.renderer.node.spec

import com.viewcompose.renderer.node.TextFieldImeAction
import com.viewcompose.renderer.node.TextFieldType

data class TextFieldNodeProps(
    val value: String,
    val label: String,
    val labelColor: Int,
    val labelTextSizeSp: Int,
    val supportingText: String,
    val supportingTextColor: Int,
    val supportingTextSizeSp: Int,
    val placeholder: String,
    val enabled: Boolean,
    val singleLine: Boolean,
    val minLines: Int,
    val maxLines: Int,
    val keyboardType: TextFieldType,
    val imeAction: TextFieldImeAction,
    val hintColor: Int,
    val readOnly: Boolean,
    val onValueChange: ((String) -> Unit)?,
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
    val maxLength: Int? = null,
    val cursorColor: Int = 0,
) : NodeSpec
