package com.viewcompose.renderer.view.tree

import android.view.View
import com.viewcompose.renderer.modifier.ResolvedModifiers
import com.viewcompose.renderer.view.container.DeclarativeCanvasLayout

internal object ModifierGraphicsApplier {
    fun applyGraphicsModifiers(
        view: View,
        resolved: ResolvedModifiers,
    ) {
        if (view !is DeclarativeCanvasLayout) return
        view.setDrawModifierElements(resolved.drawElements)
    }
}
