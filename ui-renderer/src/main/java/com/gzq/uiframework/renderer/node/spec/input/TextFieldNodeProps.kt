package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType

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
) : NodeSpec
