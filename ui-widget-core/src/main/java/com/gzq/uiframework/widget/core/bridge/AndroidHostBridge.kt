package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
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

fun Fragment.setUiContent(
    debug: Boolean = false,
    debugTag: String = "UIFramework",
    overlayHostFactory: (ViewGroup) -> OverlayHost = { OverlayHostDefaults.noOp },
    onRenderStats: ((RenderStats) -> Unit)? = null,
    onRenderResult: ((RenderTreeResult) -> Unit)? = null,
    content: UiTreeBuilder.(ViewGroup) -> Unit,
): ViewGroup {
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
    FragmentRenderSessionRegistry.bind(
        fragment = this,
        session = session,
    )
    return root
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

private object FragmentRenderSessionRegistry {
    private class Binding(
        val session: RenderSession,
    ) {
        var disposed: Boolean = false
        var fragmentObserver: DefaultLifecycleObserver? = null
        var ownerObserver: Observer<LifecycleOwner>? = null
        var viewOwner: LifecycleOwner? = null
        var viewObserver: DefaultLifecycleObserver? = null
    }

    private val bindings = WeakHashMap<Fragment, Binding>()

    fun bind(
        fragment: Fragment,
        session: RenderSession,
    ) {
        clear(fragment)
        val binding = Binding(session)
        bindings[fragment] = binding

        val fragmentObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                clear(fragment)
            }
        }
        binding.fragmentObserver = fragmentObserver
        fragment.lifecycle.addObserver(fragmentObserver)

        val ownerObserver = Observer<LifecycleOwner> { owner ->
            bindViewLifecycle(
                fragment = fragment,
                binding = binding,
                owner = owner,
            )
        }
        binding.ownerObserver = ownerObserver
        fragment.viewLifecycleOwnerLiveData.observe(fragment, ownerObserver)
        fragment.viewLifecycleOwnerLiveData.value?.let { owner ->
            bindViewLifecycle(
                fragment = fragment,
                binding = binding,
                owner = owner,
            )
        }
    }

    private fun bindViewLifecycle(
        fragment: Fragment,
        binding: Binding,
        owner: LifecycleOwner,
    ) {
        if (binding.disposed || bindings[fragment] !== binding || binding.viewOwner === owner) {
            return
        }
        binding.viewOwner?.let { previousOwner ->
            binding.viewObserver?.let(previousOwner.lifecycle::removeObserver)
        }
        val observer = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                clear(fragment)
            }
        }
        binding.viewObserver = observer
        binding.viewOwner = owner
        owner.lifecycle.addObserver(observer)
    }

    private fun clear(
        fragment: Fragment,
    ) {
        val binding = bindings.remove(fragment) ?: return
        binding.ownerObserver?.let(fragment.viewLifecycleOwnerLiveData::removeObserver)
        binding.fragmentObserver?.let(fragment.lifecycle::removeObserver)
        binding.viewOwner?.let { owner ->
            binding.viewObserver?.let(owner.lifecycle::removeObserver)
        }
        dispose(binding)
    }

    private fun dispose(
        binding: Binding,
    ) {
        if (binding.disposed) {
            return
        }
        binding.disposed = true
        binding.session.dispose()
    }
}
