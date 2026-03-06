package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.gzq.uiframework.renderer.view.tree.RenderStats
import com.gzq.uiframework.renderer.view.tree.RenderTreeResult
import java.util.WeakHashMap

data class UiContentHost(
    val root: ViewGroup,
    val session: RenderSession,
)

fun ComponentActivity.createUiContentRoot(
): FrameLayout {
    return buildUiContentRoot(
        context = this,
    )
}

fun Fragment.createUiContentRoot(
): FrameLayout {
    return buildUiContentRoot(
        context = requireContext(),
    )
}

fun ComponentActivity.setUiContent(
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    overlayHostFactory: (ViewGroup) -> OverlayHost = { OverlayHostDefaults.noOp },
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.(ViewGroup) -> Unit,
): ViewGroup {
    val root = createUiContentRoot()
    setContentView(root)
    val session = renderInto(
        container = root,
        debug = debug,
        debugTag = debugTag,
        overlayHost = overlayHostFactory(root),
        onRenderStats = onRenderStats,
        onRenderResult = onRenderResult,
    ) {
        content(root)
    }
    ActivityRenderSessionRegistry.bind(
        activity = this,
        session = session,
    )
    return root
}

fun Fragment.createUiContent(
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    overlayHostFactory: (ViewGroup) -> OverlayHost = { OverlayHostDefaults.noOp },
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.(ViewGroup) -> Unit,
): UiContentHost {
    val root = createUiContentRoot()
    val session = renderInto(
        container = root,
        debug = debug,
        debugTag = debugTag,
        overlayHost = overlayHostFactory(root),
        onRenderStats = onRenderStats,
        onRenderResult = onRenderResult,
    ) {
        content(root)
    }
    return UiContentHost(
        root = root,
        session = session,
    )
}

private fun buildUiContentRoot(
    context: Context,
): FrameLayout {
    return FrameLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }
}

private object ActivityRenderSessionRegistry {
    private val sessions = WeakHashMap<ComponentActivity, RenderSession>()
    private val observers = WeakHashMap<ComponentActivity, DefaultLifecycleObserver>()

    fun bind(
        activity: ComponentActivity,
        session: RenderSession,
    ) {
        sessions.remove(activity)?.dispose()
        observers.remove(activity)?.let(activity.lifecycle::removeObserver)

        val observer = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                sessions.remove(activity)?.dispose()
                observers.remove(activity)
                owner.lifecycle.removeObserver(this)
            }
        }
        sessions[activity] = session
        observers[activity] = observer
        activity.lifecycle.addObserver(observer)
    }
}
