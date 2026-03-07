package com.viewcompose

import android.view.ViewGroup
import com.viewcompose.widget.core.UiTreeBuilder

class WidgetShowcaseActivity : DemoRenderActivity() {
    override val demoTitle: String = "控件展示"

    override fun buildDemoContent(root: ViewGroup, builder: UiTreeBuilder) {
        builder.WidgetShowcasePage()
    }
}
