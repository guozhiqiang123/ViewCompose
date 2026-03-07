package com.gzq.uiframework

import android.content.Intent
import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class MainActivity : DemoRenderActivity() {
    override val demoTitle: String = "UIFramework Demo"

    override fun redirectTargetIntent(): Intent? {
        val moduleKey = intent?.getStringExtra(EXTRA_DEMO_MODULE_KEY)
        val targetActivity = moduleKey?.let(::findDemoModuleByKey)?.activityClass
        return targetActivity?.let {
            Intent(this, it).apply {
                intent?.extras?.let(::putExtras)
            }
        }
    }

    override fun UiTreeBuilder.buildRootScaffold(root: ViewGroup) {
        DemoHomeScaffold(root = root)
    }

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        // Not used — DemoHomeScaffold manages its own content.
    }
}
