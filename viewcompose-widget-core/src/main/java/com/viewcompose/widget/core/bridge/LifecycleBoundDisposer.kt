package com.viewcompose.widget.core

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class LifecycleBoundDisposer(
    private val onLifecycleDestroyed: () -> Unit,
) {
    private var owner: LifecycleOwner? = null
    private var observer: DefaultLifecycleObserver? = null

    fun bind(
        lifecycleOwner: LifecycleOwner,
    ) {
        if (owner === lifecycleOwner) {
            return
        }
        clearObserver()
        val nextObserver = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                onLifecycleDestroyed()
            }
        }
        owner = lifecycleOwner
        observer = nextObserver
        lifecycleOwner.lifecycle.addObserver(nextObserver)
    }

    fun clearObserver() {
        owner?.let { currentOwner ->
            observer?.let(currentOwner.lifecycle::removeObserver)
        }
        owner = null
        observer = null
    }
}
