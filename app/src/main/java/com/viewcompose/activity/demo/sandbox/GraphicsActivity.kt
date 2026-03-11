package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

class GraphicsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Graphics"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.GraphicsPage()
    }
}
