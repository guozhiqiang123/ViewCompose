package com.viewcompose.host.android.runtime

import com.viewcompose.widget.core.installCoreRenderEngine
import java.util.concurrent.atomic.AtomicBoolean

private val installed = AtomicBoolean(false)

internal fun ensureAndroidCoreRenderEngineInstalled() {
    if (installed.compareAndSet(false, true)) {
        installCoreRenderEngine(AndroidCoreRenderEngine())
    }
}
