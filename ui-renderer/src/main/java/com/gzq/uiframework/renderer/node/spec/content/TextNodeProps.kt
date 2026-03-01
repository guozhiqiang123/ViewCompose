package com.gzq.uiframework.renderer.node.spec

import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow

data class TextNodeProps(
    val text: CharSequence?,
    val maxLines: Int,
    val overflow: TextOverflow,
    val textAlign: TextAlign,
) : NodeSpec
