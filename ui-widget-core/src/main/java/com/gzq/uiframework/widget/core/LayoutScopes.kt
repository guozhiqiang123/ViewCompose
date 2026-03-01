package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.align as applyAlign
import com.gzq.uiframework.renderer.modifier.weight as applyWeight

@UiDslMarker
open class LayoutScope internal constructor() : UiTreeBuilder()

@UiDslMarker
class RowScope internal constructor() : LayoutScope() {
    fun Modifier.weight(weight: Float): Modifier = applyWeight(weight)

    fun Modifier.align(alignment: VerticalAlignment): Modifier = applyAlign(alignment)

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
    fun Modifier.weight(weight: Float): Modifier = applyWeight(weight)

    fun Modifier.align(alignment: HorizontalAlignment): Modifier = applyAlign(alignment)

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
    fun Modifier.align(alignment: BoxAlignment): Modifier = applyAlign(alignment)
}
