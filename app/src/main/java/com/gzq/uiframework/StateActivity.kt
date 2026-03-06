package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

internal const val EXTRA_STATE_PAGE_INDEX = "state_page_index"

class StateActivity : DemoRenderActivity() {
    override val demoTitle: String = "State"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.StatePage(
            initialPageIndex = intent?.getIntExtra(EXTRA_STATE_PAGE_INDEX, 0) ?: 0,
            onOpenDiagnostics = {
                startActivity(DiagnosticsActivity.newIntent(this, DiagnosticsActivity.PAGE_RENDERER))
            },
        )
    }
}
