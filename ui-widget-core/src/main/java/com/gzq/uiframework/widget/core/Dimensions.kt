package com.gzq.uiframework.widget.core

val Int.dp: Int
    get() = Environment.density.dp(this)

val Int.sp: Int
    get() = this
