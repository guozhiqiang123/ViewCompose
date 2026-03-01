package com.gzq.uiframework

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gzq.uiframework.overlay.android.host.AndroidOverlayHost
import com.gzq.uiframework.widget.core.RenderSession
import com.gzq.uiframework.widget.core.UiTreeBuilder
import com.gzq.uiframework.widget.core.renderInto

abstract class DemoRenderActivity : AppCompatActivity() {
    private var renderSession: RenderSession? = null

    protected open val showBackButton: Boolean = true

    protected open fun redirectTargetIntent(): Intent? = null

    protected abstract val demoTitle: String

    protected abstract val demoSubtitle: String

    protected abstract fun buildDemoContent(
        root: ViewGroup,
        builder: UiTreeBuilder,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (consumeRedirectIfNeeded()) {
            return
        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val root = findViewById<ViewGroup>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        renderSession?.dispose()
        renderSession = renderInto(
            container = root,
            debug = true,
            debugTag = "UIFrameworkSample",
            overlayHost = AndroidOverlayHost(root),
            onRenderResult = DemoRenderDiagnosticsStore::record,
        ) {
            DemoPageScaffold(
                root = root,
                title = demoTitle,
                subtitle = demoSubtitle,
                showBackButton = showBackButton,
            ) { builder ->
                buildDemoContent(root, builder)
            }
        }
    }

    override fun onDestroy() {
        renderSession?.dispose()
        renderSession = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeRedirectIfNeeded()
    }

    private fun consumeRedirectIfNeeded(): Boolean {
        redirectTargetIntent()?.let { targetIntent ->
            startActivity(targetIntent)
            finish()
            return true
        }
        return false
    }
}
