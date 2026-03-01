package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.RemoteImageLoader
import com.gzq.uiframework.renderer.node.RemoteImageRequest
import com.gzq.uiframework.renderer.node.TypedPropKeys
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
        assertEquals(ImageSource.Resource(42), node.props[TypedPropKeys.ImageSource])
        assertEquals("Demo image", node.props[TypedPropKeys.ImageContentDescription])
        assertEquals(ImageContentScale.Crop, node.props[TypedPropKeys.ImageContentScale])
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
        assertEquals(0xFF123456.toInt(), node.props[TypedPropKeys.ImageTint])
        assertEquals(ImageContentScale.Inside, node.props[TypedPropKeys.ImageContentScale])
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
                    placeholder = ImageSource.Resource(10),
                    error = ImageSource.Resource(11),
                    fallback = ImageSource.Resource(12),
                )
            }
        }

        val node = tree.single()

        assertEquals(ImageSource.Remote("https://example.com/demo.png"), node.props[TypedPropKeys.ImageSource])
        assertNotNull(node.props[TypedPropKeys.ImageRemoteLoader])
        assertEquals(ImageSource.Resource(10), node.props[TypedPropKeys.ImagePlaceholder])
        assertEquals(ImageSource.Resource(11), node.props[TypedPropKeys.ImageError])
        assertEquals(ImageSource.Resource(12), node.props[TypedPropKeys.ImageFallback])
    }

    @Test
    fun `remote image can emit nullable url for fallback handling`() {
        val loader = RemoteImageLoader { _, _: RemoteImageRequest -> }
        val tree = buildVNodeTree {
            ProvideRemoteImageLoader(loader) {
                Image(
                    source = ImageSource.Remote(null),
                    fallback = ImageSource.Resource(99),
                )
            }
        }

        val node = tree.single()

        assertEquals(ImageSource.Remote(null), node.props[TypedPropKeys.ImageSource])
        assertEquals(ImageSource.Resource(99), node.props[TypedPropKeys.ImageFallback])
    }

    private fun com.gzq.uiframework.renderer.modifier.Modifier.readModifierElements(): List<Any?> {
        val field = javaClass.getDeclaredField("elements")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        return field.get(this) as List<Any?>
    }
}
