package com.viewcompose.host.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

class LifecycleBoundDisposerTest {
    @Test
    fun `invokes callback when bound lifecycle is destroyed`() {
        var disposeCount = 0
        val disposer = LifecycleBoundDisposer { disposeCount += 1 }
        val owner = TestLifecycleOwner()

        owner.handle(Lifecycle.Event.ON_CREATE)
        disposer.bind(owner)
        owner.handle(Lifecycle.Event.ON_DESTROY)

        assertEquals(1, disposeCount)
    }

    @Test
    fun `rebind detaches previous lifecycle observer`() {
        var disposeCount = 0
        val disposer = LifecycleBoundDisposer { disposeCount += 1 }
        val ownerA = TestLifecycleOwner()
        val ownerB = TestLifecycleOwner()

        ownerA.handle(Lifecycle.Event.ON_CREATE)
        ownerB.handle(Lifecycle.Event.ON_CREATE)
        disposer.bind(ownerA)
        disposer.bind(ownerB)

        ownerA.handle(Lifecycle.Event.ON_DESTROY)
        assertEquals(0, disposeCount)

        ownerB.handle(Lifecycle.Event.ON_DESTROY)
        assertEquals(1, disposeCount)
    }

    @Test
    fun `clearObserver cancels disposal callback`() {
        var disposeCount = 0
        val disposer = LifecycleBoundDisposer { disposeCount += 1 }
        val owner = TestLifecycleOwner()

        owner.handle(Lifecycle.Event.ON_CREATE)
        disposer.bind(owner)
        disposer.clearObserver()
        owner.handle(Lifecycle.Event.ON_DESTROY)

        assertEquals(0, disposeCount)
    }
}

private class TestLifecycleOwner : LifecycleOwner {
    private val registry = LifecycleRegistry.createUnsafe(this)

    override val lifecycle: Lifecycle
        get() = registry

    fun handle(
        event: Lifecycle.Event,
    ) {
        registry.handleLifecycleEvent(event)
    }
}
