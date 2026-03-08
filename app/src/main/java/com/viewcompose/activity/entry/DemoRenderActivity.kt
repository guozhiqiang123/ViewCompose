package com.viewcompose

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.viewcompose.host.android.setUiContent
import com.viewcompose.overlay.android.host.AndroidOverlayHost
import com.viewcompose.widget.core.UiTreeBuilder

abstract class DemoRenderActivity : AppCompatActivity() {
    protected open fun redirectTargetIntent(): Intent? = null

    protected abstract val demoTitle: String

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
        setUiContent(
            debug = true,
            debugTag = "ViewComposeSample",
            overlayHostFactory = ::AndroidOverlayHost,
            onRenderResult = DemoRenderDiagnosticsStore::record,
        ) { root ->
            buildRootScaffold(root)
        }
    }

    protected open fun UiTreeBuilder.buildRootScaffold(root: ViewGroup) {
        DemoSubPageScaffold(
            root = root,
            title = demoTitle,
        ) { builder ->
            buildDemoContent(root, builder)
        }
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
