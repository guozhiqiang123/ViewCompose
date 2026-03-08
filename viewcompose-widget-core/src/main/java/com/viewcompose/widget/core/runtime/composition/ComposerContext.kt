package com.viewcompose.widget.core

import com.viewcompose.runtime.composition.ComposerLite

internal object ComposerContext {
    private val currentComposer = ThreadLocal<ComposerLite?>()

    fun <T> withComposer(
        composer: ComposerLite,
        block: () -> T,
    ): T {
        val previous = currentComposer.get()
        currentComposer.set(composer)
        return try {
            block()
        } finally {
            currentComposer.set(previous)
        }
    }

    fun currentComposer(): ComposerLite? = currentComposer.get()
}
