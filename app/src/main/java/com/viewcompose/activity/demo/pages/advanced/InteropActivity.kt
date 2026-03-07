package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

class InteropActivity : DemoRenderActivity() {
    override val demoTitle: String = "Interop"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.InteropPage()
    }
}
