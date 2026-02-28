package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ImageTest {
    @Test
    fun `image emits semantic props`() {
        val tree = buildVNodeTree {
            Image(
                source = ImageSource.Resource(42),
                contentDescription = "Demo image",
                contentScale = ImageContentScale.Crop,
            )
        }

        val node = tree.single()

        assertEquals(NodeType.Image, node.type)
        assertEquals(ImageSource.Resource(42), node.props.values[PropKeys.IMAGE_SOURCE])
        assertEquals("Demo image", node.props.values[PropKeys.IMAGE_CONTENT_DESCRIPTION])
        assertEquals(ImageContentScale.Crop, node.props.values[PropKeys.IMAGE_CONTENT_SCALE])
    }

    @Test
    fun `icon inherits local content color and default size`() {
        val tree = buildVNodeTree {
            ProvideContentColor(0xFF123456.toInt()) {
                Icon(
                    source = ImageSource.Resource(12),
                    contentDescription = "Local icon",
                )
            }
        }

        val node = tree.single()
        val size = node.modifier.readModifierElements().last { it is SizeModifierElement } as SizeModifierElement

        assertEquals(NodeType.Image, node.type)
        assertEquals(0xFF123456.toInt(), node.props.values[PropKeys.IMAGE_TINT])
        assertEquals(ImageContentScale.Inside, node.props.values[PropKeys.IMAGE_CONTENT_SCALE])
        assertEquals(24.dp, size.width)
        assertEquals(24.dp, size.height)
    }

    @Test
    fun `remote image inherits scoped loader`() {
        val loader = RemoteImageLoader { _, _ -> }
        val tree = buildVNodeTree {
            ProvideRemoteImageLoader(loader) {
                Image(
                    source = ImageSource.Remote("https://example.com/demo.png"),
                )
            }
        }

        val node = tree.single()

        assertEquals(ImageSource.Remote("https://example.com/demo.png"), node.props.values[PropKeys.IMAGE_SOURCE])
        assertNotNull(node.props.values[PropKeys.IMAGE_REMOTE_LOADER])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
