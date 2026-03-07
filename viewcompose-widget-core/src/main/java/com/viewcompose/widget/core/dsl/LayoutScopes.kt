package com.viewcompose.widget.core

import com.viewcompose.renderer.layout.BoxAlignment
import com.viewcompose.renderer.layout.HorizontalAlignment
import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.modifier.BoxAlignModifierElement
import com.viewcompose.renderer.modifier.HorizontalAlignModifierElement
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.VerticalAlignModifierElement
import com.viewcompose.renderer.modifier.WeightModifierElement

@UiDslMarker
open class LayoutScope internal constructor() : UiTreeBuilder()

@UiDslMarker
class RowScope internal constructor() : LayoutScope() {
    fun Modifier.weight(weight: Float): Modifier = scopedWeight(weight)

    fun Modifier.align(alignment: VerticalAlignment): Modifier = then(VerticalAlignModifierElement(alignment))

    fun FlexibleSpacer(
        weight: Float = 1f,
        key: Any? = null,
        modifier: Modifier = Modifier,
    ) {
        Spacer(
            key = key,
            modifier = modifier.weight(weight),
        )
    }
}

@UiDslMarker
class ColumnScope internal constructor() : LayoutScope() {
    fun Modifier.weight(weight: Float): Modifier = scopedWeight(weight)

    fun Modifier.align(alignment: HorizontalAlignment): Modifier = then(HorizontalAlignModifierElement(alignment))

    fun FlexibleSpacer(
        weight: Float = 1f,
        key: Any? = null,
        modifier: Modifier = Modifier,
    ) {
        Spacer(
            key = key,
            modifier = modifier.weight(weight),
        )
    }
}

@UiDslMarker
class BoxScope internal constructor() : LayoutScope() {
    fun Modifier.align(alignment: BoxAlignment): Modifier = then(BoxAlignModifierElement(alignment))
}

private fun Modifier.scopedWeight(weight: Float): Modifier {
    require(weight > 0f) {
        "weight must be > 0"
    }
    return then(WeightModifierElement(weight))
}
