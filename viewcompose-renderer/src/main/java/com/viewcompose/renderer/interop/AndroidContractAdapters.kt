package com.viewcompose.renderer.interop

import android.graphics.Typeface
import com.viewcompose.ui.node.PlatformRemoteImageTarget
import com.viewcompose.ui.node.PlatformRenderContainerHandle
import com.viewcompose.ui.node.spec.PlatformUiFontFamily
import com.viewcompose.ui.node.spec.UiFontFamily

internal data class AndroidRenderContainerHandle(
    override val container: Any,
) : PlatformRenderContainerHandle

internal data class AndroidRemoteImageTarget(
    override val target: Any,
) : PlatformRemoteImageTarget

internal fun UiFontFamily?.toTypefaceOrNull(): Typeface? {
    return (this as? PlatformUiFontFamily)?.font as? Typeface
}
