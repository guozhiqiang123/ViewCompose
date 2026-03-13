package com.viewcompose

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import com.viewcompose.runtime.mutableStateOf
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.remember

class DiagnosticsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Diagnostics"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        val initialPage = intent.getIntExtra(EXTRA_PAGE, PAGE_RUNTIME)
        val autoRefreshRendererSnapshot = intent.getBooleanExtra(
            EXTRA_AUTO_REFRESH_RENDERER_SNAPSHOT,
            false,
        )
        val entryHint = intent.getStringExtra(EXTRA_ENTRY_HINT)
        with(builder) {
            val selectedPageState = remember { mutableStateOf(initialPage) }
            DiagnosticsPage(
                selectedPageState = selectedPageState,
                autoRefreshOnEnter = autoRefreshRendererSnapshot,
                entryHint = entryHint,
            )
        }
    }

    companion object {
        private const val EXTRA_PAGE = "page"
        private const val EXTRA_AUTO_REFRESH_RENDERER_SNAPSHOT = "auto_refresh_renderer_snapshot"
        private const val EXTRA_ENTRY_HINT = "entry_hint"

        const val PAGE_RUNTIME = 0
        const val PAGE_THEME = 1
        const val PAGE_RENDERER = 2
        const val PAGE_GAPS = 3

        fun newIntent(
            context: Context,
            page: Int = PAGE_RUNTIME,
            autoRefreshRendererSnapshot: Boolean = false,
            entryHint: String? = null,
        ): Intent {
            return Intent(context, DiagnosticsActivity::class.java)
                .putExtra(EXTRA_PAGE, page)
                .putExtra(EXTRA_AUTO_REFRESH_RENDERER_SNAPSHOT, autoRefreshRendererSnapshot)
                .apply {
                    if (entryHint != null) {
                        putExtra(EXTRA_ENTRY_HINT, entryHint)
                    }
                }
        }
    }
}
