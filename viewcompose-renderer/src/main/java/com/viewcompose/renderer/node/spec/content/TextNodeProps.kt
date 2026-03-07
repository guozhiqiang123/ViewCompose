package com.viewcompose.renderer.node.spec

import android.graphics.Typeface
import com.viewcompose.renderer.node.TextAlign
import com.viewcompose.renderer.node.TextDecoration
import com.viewcompose.renderer.node.TextOverflow

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
    val textDecoration: TextDecoration = TextDecoration.None,
) : NodeSpec
