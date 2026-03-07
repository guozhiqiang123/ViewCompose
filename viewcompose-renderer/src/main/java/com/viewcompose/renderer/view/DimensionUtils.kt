package com.viewcompose.renderer.view

import android.content.Context

internal fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()
