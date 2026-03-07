package com.viewcompose.renderer.node.spec

import android.content.Context
import android.view.View

data class AndroidViewNodeProps(
    val factory: (Context) -> View,
    val update: ((View) -> Unit)?,
) : NodeSpec
