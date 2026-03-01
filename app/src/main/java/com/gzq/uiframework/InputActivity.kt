package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class InputActivity : DemoRenderActivity() {
    override val demoTitle: String = "Input"

    override val demoSubtitle: String =
        "Text fields, selection controls, disabled states, and form-oriented stress scenarios."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.InputPage()
    }
}
