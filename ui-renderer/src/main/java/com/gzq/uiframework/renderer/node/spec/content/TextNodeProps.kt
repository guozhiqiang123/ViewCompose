package com.gzq.uiframework.renderer.node.spec

import android.graphics.Typeface
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextOverflow

data class TextNodeProps(
    val text: CharSequence?,
    val maxLines: Int,
    val overflow: TextOverflow,
    val textAlign: TextAlign,
    val textColor: Int,
    val textSizeSp: Int,
    val fontWeight: Int? = null,
    val fontFamily: Typeface? = null,
    val letterSpacingEm: Float? = null,
    val lineHeightSp: Int? = null,
    val includeFontPadding: Boolean = false,
) : NodeSpec
