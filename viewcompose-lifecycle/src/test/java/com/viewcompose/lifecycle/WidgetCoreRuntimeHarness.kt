package com.viewcompose.lifecycle

import com.viewcompose.runtime.State
import com.viewcompose.widget.core.UiTreeBuilder
import com.viewcompose.widget.core.buildVNodeTree
import java.lang.reflect.Method

internal class WidgetCoreRuntimeHarness {
    private val rememberStoreClass = Class.forName("com.viewcompose.widget.core.RememberStore")
    private val effectStoreClass = Class.forName("com.viewcompose.widget.core.EffectStore")
    private val rememberContextClass = Class.forName("com.viewcompose.widget.core.RememberContext")
    private val effectContextClass = Class.forName("com.viewcompose.widget.core.EffectContext")

    private val rememberStore: Any = rememberStoreClass.getDeclaredConstructor().newInstance()
    private val effectStore: Any = effectStoreClass.getDeclaredConstructor().newInstance()
    private val rememberContextInstance: Any = requireNotNull(
        rememberContextClass.getField("INSTANCE").get(null),
    )
    private val effectContextInstance: Any = requireNotNull(
        effectContextClass.getField("INSTANCE").get(null),
    )

    private val rememberWithStore = rememberContextClass.findMethodPrefix(
        prefix = "withStore",
        paramCount = 2,
    )
    private val effectWithStore = effectContextClass.findMethodPrefix(
        prefix = "withStore",
        paramCount = 2,
    )
    private val effectCommit = effectStoreClass.findMethodPrefix(
        prefix = "commit",
        paramCount = 0,
    )
    private val effectDisposeAll = effectStoreClass.findMethodPrefix(
        prefix = "disposeAll",
        paramCount = 0,
    )

    fun <T> render(
        block: () -> State<T>,
    ): State<T> {
        val state = withStore(effectContextInstance, effectWithStore, effectStore) {
            withStore(rememberContextInstance, rememberWithStore, rememberStore) {
                block()
            }
        }
        effectCommit.invoke(effectStore)
        return state
    }

    fun renderTree(
        block: UiTreeBuilder.() -> Unit,
    ) {
        withStore(effectContextInstance, effectWithStore, effectStore) {
            withStore(rememberContextInstance, rememberWithStore, rememberStore) {
                buildVNodeTree(block)
            }
        }
        effectCommit.invoke(effectStore)
    }

    fun dispose() {
        effectDisposeAll.invoke(effectStore)
    }

    private fun <T> withStore(
        contextInstance: Any,
        method: Method,
        store: Any,
        block: () -> T,
    ): T {
        val callback = object : kotlin.jvm.functions.Function0<T> {
            override fun invoke(): T = block()
        }
        @Suppress("UNCHECKED_CAST")
        return method.invoke(contextInstance, store, callback) as T
    }

    private fun Class<*>.findMethodPrefix(
        prefix: String,
        paramCount: Int,
    ): Method {
        val method = methods.firstOrNull { candidate ->
            candidate.name.startsWith(prefix) && candidate.parameterCount == paramCount
        } ?: declaredMethods.firstOrNull { candidate ->
            candidate.name.startsWith(prefix) && candidate.parameterCount == paramCount
        } ?: error("Method with prefix '$prefix' and $paramCount params not found in $name")
        method.isAccessible = true
        return method
    }
}
