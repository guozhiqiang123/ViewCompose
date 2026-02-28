package com.gzq.uiframework.widget.core

import kotlin.math.roundToInt

val Int.dp: Int
    get() = Environment.density.dp(this)

val Float.dp: Int
    get() = Environment.density.dp(roundToInt())

val Int.sp: Int
    get() = this

val Float.sp: Int
    get() = roundToInt()
