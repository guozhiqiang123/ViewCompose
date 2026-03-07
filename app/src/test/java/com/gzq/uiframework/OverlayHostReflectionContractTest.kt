package com.gzq.uiframework

import android.view.View
import com.gzq.uiframework.widget.core.OverlayHost
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OverlayHostReflectionContractTest {
    @Test
    fun androidOverlayHost_classAndConstructor_matchReflectionContract() {
        val className = "com.gzq.uiframework.overlay.android.host.AndroidOverlayHost"
        val clazz = Class.forName(className)
        assertTrue(OverlayHost::class.java.isAssignableFrom(clazz))
        val constructor = clazz.getConstructor(View::class.java)
        assertNotNull(constructor)
    }
}
