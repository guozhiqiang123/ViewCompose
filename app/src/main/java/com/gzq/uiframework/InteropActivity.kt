package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class InteropActivity : DemoRenderActivity() {
    override val demoTitle: String = "Interop"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.InteropPage()
    }
}
