package com.viewcompose.host.android

import android.view.View
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.NativeViewElement
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.AndroidViewNodeProps
import com.viewcompose.widget.core.buildVNodeTree
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidInteropDslTest {
    @Test
    fun `android view emits android view node spec`() {
        val tree = buildVNodeTree {
            AndroidView(
                factory = { _: android.content.Context ->
                    throw IllegalStateException("factory should not be invoked during tree build")
                },
                update = { _: View -> Unit },
            )
        }

        val node = tree.single()
        assertEquals(NodeType.AndroidView, node.type)
        assertTrue(node.spec is AndroidViewNodeProps)
    }

    @Test
    fun `nativeView adds native view modifier element`() {
        val modifier = Modifier.nativeView(key = "host") { _: View -> Unit }
        assertEquals(1, modifier.elements.size)
        assertTrue(modifier.elements.single() is NativeViewElement)
    }
}
