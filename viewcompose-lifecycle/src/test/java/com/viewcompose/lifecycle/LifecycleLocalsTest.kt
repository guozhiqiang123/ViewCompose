package com.viewcompose.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.viewcompose.widget.core.buildVNodeTree
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class LifecycleLocalsTest {
    @Test
    fun `local lifecycle owner defaults to null`() {
        assertNull(LocalLifecycleOwner.current)
    }

    @Test
    fun `provide lifecycle owner publishes value and restores after scope`() {
        val owner = TestLifecycleOwner()
        var inside: LifecycleOwner? = null

        buildVNodeTree {
            ProvideLifecycleOwner(owner) {
                inside = LocalLifecycleOwner.current
            }
        }

        assertSame(owner, inside)
        assertNull(LocalLifecycleOwner.current)
    }

    private class TestLifecycleOwner : LifecycleOwner {
        private val registry = LifecycleRegistry.createUnsafe(this)

        override val lifecycle: Lifecycle
            get() = registry
    }
}
