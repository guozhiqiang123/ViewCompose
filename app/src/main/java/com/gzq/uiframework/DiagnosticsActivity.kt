package com.gzq.uiframework

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import com.gzq.uiframework.runtime.mutableStateOf
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.remember

class DiagnosticsActivity : DemoRenderActivity() {
    override val demoTitle: String = "Diagnostics"

    override fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    ) {
        val initialPage = intent.getIntExtra(EXTRA_PAGE, PAGE_RUNTIME)
        with(builder) {
            val selectedPageState = remember { mutableStateOf(initialPage) }
            DiagnosticsPage(selectedPageState = selectedPageState)
        }
    }

    companion object {
        private const val EXTRA_PAGE = "page"

        const val PAGE_RUNTIME = 0
        const val PAGE_RENDERER = 1
        const val PAGE_GAPS = 2

        fun newIntent(
            context: Context,
            page: Int = PAGE_RUNTIME,
        ): Intent {
            return Intent(context, DiagnosticsActivity::class.java)
                .putExtra(EXTRA_PAGE, page)
        }
    }
}
