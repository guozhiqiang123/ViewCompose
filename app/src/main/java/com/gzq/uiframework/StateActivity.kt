package com.gzq.uiframework

import android.view.ViewGroup
import com.gzq.uiframework.widget.core.UiTreeBuilder

class StateActivity : DemoRenderActivity() {
    override val demoTitle: String = "State"

    override val demoSubtitle: String =
        "remember, derived state, effects, identity, and patch stress paths."

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        builder.StatePage(
            onOpenDiagnostics = {
                startActivity(DiagnosticsActivity.newIntent(this, DiagnosticsActivity.PAGE_RENDERER))
            },
        )
    }
}
