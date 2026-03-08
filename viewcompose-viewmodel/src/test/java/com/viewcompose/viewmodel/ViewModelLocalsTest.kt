package com.viewcompose.viewmodel

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.viewcompose.widget.core.buildVNodeTree
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ViewModelLocalsTest {
    @Test
    fun `local view model owner defaults to null`() {
        assertNull(LocalViewModelStoreOwner.current)
    }

    @Test
    fun `provide view model owner publishes value and restores after scope`() {
        val owner = TestViewModelStoreOwner()
        var inside: ViewModelStoreOwner? = null

        buildVNodeTree {
            ProvideViewModelStoreOwner(owner) {
                inside = LocalViewModelStoreOwner.current
            }
        }

        assertSame(owner, inside)
        assertNull(LocalViewModelStoreOwner.current)
        owner.viewModelStore.clear()
    }

    private class TestViewModelStoreOwner : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
