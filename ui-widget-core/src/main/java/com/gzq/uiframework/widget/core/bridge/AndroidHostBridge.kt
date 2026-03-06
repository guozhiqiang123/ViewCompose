package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    applySystemBarsInsetsPadding: Boolean = true,
): FrameLayout {
    return buildUiContentRoot(
        context = this,
        applySystemBarsInsetsPadding = applySystemBarsInsetsPadding,
    )
}

fun Fragment.createUiContentRoot(
    applySystemBarsInsetsPadding: Boolean = false,
): FrameLayout {
    return buildUiContentRoot(
        context = requireContext(),
        applySystemBarsInsetsPadding = applySystemBarsInsetsPadding,
    )
}

fun ComponentActivity.setUiContent(
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    applySystemBarsInsetsPadding: Boolean = true,
    overlayHostFactory: (ViewGroup) -> OverlayHost = { OverlayHostDefaults.noOp },
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.(ViewGroup) -> Unit,
): ViewGroup {
    val root = createUiContentRoot(
        applySystemBarsInsetsPadding = applySystemBarsInsetsPadding,
    )
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
    applySystemBarsInsetsPadding: Boolean = false,
    overlayHostFactory: (ViewGroup) -> OverlayHost = { OverlayHostDefaults.noOp },
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.(ViewGroup) -> Unit,
): UiContentHost {
    val root = createUiContentRoot(
        applySystemBarsInsetsPadding = applySystemBarsInsetsPadding,
    )
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
    applySystemBarsInsetsPadding: Boolean,
): FrameLayout {
    return FrameLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        if (applySystemBarsInsetsPadding) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
            requestApplyInsetsWhenAttached()
        }
    }
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
        return
    }
    addOnAttachStateChangeListener(
        object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                view.removeOnAttachStateChangeListener(this)
                view.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(view: View) = Unit
        },
    )
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
