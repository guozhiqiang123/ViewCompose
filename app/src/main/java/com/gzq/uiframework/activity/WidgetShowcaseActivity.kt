package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class WidgetShowcaseActivity : DemoRenderActivity() {
    override val demoTitle: String = "控件展示"

    override fun buildDemoContent(root: ViewGroup, builder: UiTreeBuilder) {
        builder.WidgetShowcasePage()
    }
}
