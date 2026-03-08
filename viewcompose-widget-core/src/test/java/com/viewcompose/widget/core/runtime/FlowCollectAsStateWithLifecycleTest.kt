package com.viewcompose.widget.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.viewcompose.lifecycle.ProvideLifecycleOwner
import com.viewcompose.lifecycle.collectAsStateWithLifecycle
import com.viewcompose.runtime.State
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowCollectAsStateWithLifecycleTest {
    @Test
    fun `collectAsStateWithLifecycle starts and stops with lifecycle state`() = runBlocking {
        val harness = LifecycleCollectHarness()
        val owner = TestLifecycleOwner()
        val source = MutableStateFlow(10)

        owner.handle(Lifecycle.Event.ON_CREATE)
        val state = harness.render {
            source.collectAsStateWithLifecycle(
                initial = -1,
                lifecycle = owner.lifecycle,
                context = Dispatchers.Unconfined,
            )
        }
        assertEquals(-1, state.value)

        owner.handle(Lifecycle.Event.ON_START)
        awaitValue(state) { it == 10 }

        source.value = 11
        awaitValue(state) { it == 11 }

        owner.handle(Lifecycle.Event.ON_STOP)
        source.value = 12
        delay(50)
        assertEquals(11, state.value)

        owner.handle(Lifecycle.Event.ON_START)
        awaitValue(state) { it == 12 }
        harness.dispose()
    }

    @Test
    fun `collectAsStateWithLifecycle resolves lifecycle owner from local`() = runBlocking {
        val harness = LifecycleCollectHarness()
        val owner = TestLifecycleOwner()
        val source = MutableStateFlow(1)
        lateinit var state: State<Int>

        owner.handle(Lifecycle.Event.ON_CREATE)
        owner.handle(Lifecycle.Event.ON_START)
        harness.renderTree {
            ProvideLifecycleOwner(owner) {
                state = source.collectAsStateWithLifecycle(
                    initial = 0,
                    context = Dispatchers.Unconfined,
                )
            }
        }
        awaitValue(state) { it == 1 }

        source.value = 2
        awaitValue(state) { it == 2 }
        harness.dispose()
    }

    @Test
    fun `collectAsStateWithLifecycle throws when lifecycle owner is missing`() {
        val source = MutableStateFlow(1)
        val error = runCatching {
            source.collectAsStateWithLifecycle(initial = 0)
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertTrue(error?.message.orEmpty().contains("ProvideLifecycleOwner"))
    }

    @Test
    fun `collectAsStateWithLifecycle cancels collector on dispose`() = runBlocking {
        val harness = LifecycleCollectHarness()
        val owner = TestLifecycleOwner()
        var canceled = 0
        val source = flow {
            emit(1)
            try {
                awaitCancellation()
            } finally {
                canceled += 1
            }
        }

        owner.handle(Lifecycle.Event.ON_CREATE)
        owner.handle(Lifecycle.Event.ON_START)
        harness.render {
            source.collectAsStateWithLifecycle(
                initial = 0,
                lifecycle = owner.lifecycle,
                context = Dispatchers.Unconfined,
            )
        }
        harness.dispose()

        withTimeout(1.seconds) {
            while (canceled == 0) {
                delay(10)
            }
        }
        assertEquals(1, canceled)
    }

    private suspend fun <T> awaitValue(
        state: State<T>,
        predicate: (T) -> Boolean,
    ) {
        withTimeout(1.seconds) {
            while (!predicate(state.value)) {
                delay(10)
            }
        }
    }

    private class LifecycleCollectHarness {
        private val rememberStore = RememberStore()
        private val effectStore = EffectStore()

        fun <T> render(
            block: () -> State<T>,
        ): State<T> {
            lateinit var state: State<T>
            EffectContext.withStore(effectStore) {
                RememberContext.withStore(rememberStore) {
                    state = block()
                }
            }
            effectStore.commit()
            return state
        }

        fun renderTree(
            block: UiTreeBuilder.() -> Unit,
        ) {
            EffectContext.withStore(effectStore) {
                RememberContext.withStore(rememberStore) {
                    buildVNodeTree(block)
                }
            }
            effectStore.commit()
        }

        fun dispose() {
            effectStore.disposeAll()
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
}
