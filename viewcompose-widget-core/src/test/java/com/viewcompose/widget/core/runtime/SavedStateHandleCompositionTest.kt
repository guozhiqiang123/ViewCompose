package com.viewcompose.widget.core

import org.junit.Assert.assertTrue
import org.junit.Test

class SavedStateHandleCompositionTest {
    @Test
    fun `savedStateHandle throws when owner is missing`() {
        val error = runCatching {
            savedStateHandle()
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
        assertTrue(error?.message.orEmpty().contains("ProvideViewModelStoreOwner"))
    }
}
