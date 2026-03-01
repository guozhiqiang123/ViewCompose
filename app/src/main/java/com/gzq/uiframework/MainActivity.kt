package com.gzq.uiframework

import android.content.Intent
import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class MainActivity : DemoRenderActivity() {
    override val showBackButton: Boolean = false

    override val demoTitle: String = "UIFramework Demo"

    override val demoSubtitle: String =
        "Module catalog aligned with Compose Tutorials categories and optimized for manual testing plus benchmark entry."

    override fun redirectTargetIntent(): Intent? {
        val moduleKey = intent?.getStringExtra(EXTRA_DEMO_MODULE_KEY)
        val targetActivity = moduleKey?.let(::findDemoModuleByKey)?.activityClass
        return targetActivity?.let { Intent(this, it) }
    }

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.DemoCatalogPage(root)
    }
}
