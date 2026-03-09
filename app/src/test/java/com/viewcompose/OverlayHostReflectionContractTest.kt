package com.viewcompose

import com.viewcompose.widget.core.OverlayHostFactoryProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.ServiceLoader

class OverlayHostReflectionContractTest {
    @Test
    fun androidOverlayHostProvider_isDiscoverableViaServiceLoader() {
        val providers = ServiceLoader.load(
            OverlayHostFactoryProvider::class.java,
            OverlayHostFactoryProvider::class.java.classLoader,
        ).toList()

        assertTrue(
            "Missing Android overlay host service provider.",
            providers.any { provider ->
                provider::class.java.name == "com.viewcompose.overlay.android.host.AndroidOverlayHostFactoryProvider"
            },
        )
    }
}
