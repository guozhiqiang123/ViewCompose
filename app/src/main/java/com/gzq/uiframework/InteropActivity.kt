package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class InteropActivity : DemoRenderActivity() {
    override val demoTitle: String = "Interop"

    override val demoSubtitle: String =
        "AndroidView mapping, themed native views, and current interop boundaries."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.InteropPage()
    }
}
