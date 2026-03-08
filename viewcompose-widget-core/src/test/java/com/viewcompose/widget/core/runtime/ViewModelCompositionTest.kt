package com.viewcompose.widget.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewModelCompositionTest {
    @Test
    fun `viewModel reuses instance across renders when owner is stable`() {
        val owner = TestViewModelStoreOwner()
        val rememberStore = RememberStore()
        var first: TestViewModel? = null
        var second: TestViewModel? = null

        RememberContext.withStore(rememberStore) {
            buildVNodeTree {
                ProvideViewModelStoreOwner(owner) {
                    first = viewModel()
                }
            }
        }
        RememberContext.withStore(rememberStore) {
            buildVNodeTree {
                ProvideViewModelStoreOwner(owner) {
                    second = viewModel()
                }
            }
        }

        assertSame(first, second)
        owner.viewModelStore.clear()
    }

    @Test
    fun `viewModel returns different instances for different keys`() {
        val owner = TestViewModelStoreOwner()
        val rememberStore = RememberStore()
        var first: TestViewModel? = null
        var second: TestViewModel? = null

        RememberContext.withStore(rememberStore) {
            buildVNodeTree {
                ProvideViewModelStoreOwner(owner) {
                    first = viewModel(key = "first")
                    second = viewModel(key = "second")
                }
            }
        }

        assertNotSame(first, second)
        owner.viewModelStore.clear()
    }

    @Test
    fun `viewModel throws when owner is missing`() {
        val error = runCatching {
            viewModel<TestViewModel>()
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertTrue(error?.message.orEmpty().contains("ProvideViewModelStoreOwner"))
    }

    @Test
    fun `viewModel uses custom factory and keeps same instance across renders`() {
        val owner = TestViewModelStoreOwner()
        val rememberStore = RememberStore()
        var createCount = 0
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                createCount += 1
                return FactoryBackedViewModel(createCount) as T
            }
        }
        var first: FactoryBackedViewModel? = null
        var second: FactoryBackedViewModel? = null

        RememberContext.withStore(rememberStore) {
            buildVNodeTree {
                ProvideViewModelStoreOwner(owner) {
                    first = viewModel(factory = factory)
                }
            }
        }
        RememberContext.withStore(rememberStore) {
            buildVNodeTree {
                ProvideViewModelStoreOwner(owner) {
                    second = viewModel(factory = factory)
                }
            }
        }

        assertSame(first, second)
        assertEquals(1, createCount)
        owner.viewModelStore.clear()
    }

    class TestViewModel : ViewModel()

    class FactoryBackedViewModel(
        val index: Int,
    ) : ViewModel()

    class TestViewModelStoreOwner : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
