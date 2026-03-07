package com.viewcompose.widget.core

import com.viewcompose.renderer.node.VNode
import org.junit.Assert.assertEquals
import org.junit.Test

class OverlayScrimThemeDefaultsTest {
    @Test
    fun `dialog default scrim opacity follows theme overlays`() {
        val store = OverlayRequestStore()
        val customTheme = UiThemeDefaults.light().copy(
            overlays = UiOverlays(scrimOpacity = 0.56f),
        )

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                UiTheme(tokens = customTheme) {
                    Dialog(
                        visible = true,
                        requestKey = "dialog_theme_scrim",
                    ) {
                        Text(text = "Dialog body")
                    }
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        val request = store.currentRequests().single()
        val spec = request.payload as DialogOverlaySpec
        assertEquals(0.56f, spec.scrimOpacity)
    }

    @Test
    fun `bottom sheet default scrim opacity follows theme overlays`() {
        val store = OverlayRequestStore()
        val customTheme = UiThemeDefaults.light().copy(
            overlays = UiOverlays(scrimOpacity = 0.61f),
        )

        val tree = OverlayRequestContext.withStore(store) {
            buildVNodeTree {
                UiTheme(tokens = customTheme) {
                    ModalBottomSheet(
                        visible = true,
                        requestKey = "sheet_theme_scrim",
                    ) {
                        Text(text = "Sheet body")
                    }
                }
            }
        }

        assertEquals(emptyList<VNode>(), tree)
        val request = store.currentRequests().single()
        val spec = request.payload as ModalBottomSheetOverlaySpec
        assertEquals(0.61f, spec.scrimOpacity)
    }
}
