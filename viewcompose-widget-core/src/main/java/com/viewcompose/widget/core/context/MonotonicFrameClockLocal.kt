package com.viewcompose.widget.core

import com.viewcompose.runtime.frame.FallbackMonotonicFrameClock
import com.viewcompose.runtime.frame.MonotonicFrameClock

private val LocalMonotonicFrameClockValue = uiLocalOf<MonotonicFrameClock> { FallbackMonotonicFrameClock }

object LocalMonotonicFrameClock {
    val current: MonotonicFrameClock
        get() = UiLocals.current(LocalMonotonicFrameClockValue)
}

fun UiTreeBuilder.ProvideMonotonicFrameClock(
    clock: MonotonicFrameClock,
    content: UiTreeBuilder.() -> Unit,
) {
    ProvideLocal(
        local = LocalMonotonicFrameClockValue,
        value = clock,
        content = content,
    )
}
