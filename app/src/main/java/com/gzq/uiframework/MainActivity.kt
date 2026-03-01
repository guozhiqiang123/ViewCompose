package com.gzq.uiframework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class MainActivity : DemoRenderActivity() {
    override val showBackButton: Boolean = false

    override val demoTitle: String = "UIFramework Demo"

    override val demoSubtitle: String =
        "Module catalog aligned with Compose Tutorials categories and optimized for manual testing plus benchmark entry."

    override fun onCreate(savedInstanceState: Bundle?) {
        val moduleKey = intent?.getStringExtra(EXTRA_DEMO_MODULE_KEY)
        val targetActivity = moduleKey?.let(::findAvailableDemoModuleByKey)?.activityClass
        if (targetActivity != null) {
            startActivity(Intent(this, targetActivity))
            finish()
            return
        }
        super.onCreate(savedInstanceState)
    }

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.DemoCatalogPage(root)
    }
}
