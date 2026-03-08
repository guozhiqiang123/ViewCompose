package com.viewcompose.renderer.layout

import com.viewcompose.ui.layout.MainAxisArrangement
import kotlin.math.max

internal data class LinearChildSpec(
    val size: Int,
    val leadingMargin: Int,
    val trailingMargin: Int,
)

internal data class LinearChildPlacement(
    val leading: Int,
    val trailing: Int,
)

internal object LinearPlacementCalculator {
    fun calculate(
        containerSize: Int,
        arrangement: MainAxisArrangement,
        itemSpacing: Int,
        hasWeightedChildren: Boolean,
        children: List<LinearChildSpec>,
    ): List<LinearChildPlacement> {
        if (children.isEmpty()) {
            return emptyList()
        }
        val consumedSize = children.sumOf { child ->
            child.size + child.leadingMargin + child.trailingMargin
        }
        val baseSpacing = if (children.size > 1) itemSpacing * (children.size - 1) else 0
        val extraSpace = max(0, containerSize - consumedSize - baseSpacing)
        val arrangementMetrics = LinearArrangementCalculator.calculate(
            arrangement = arrangement,
            itemSpacing = itemSpacing,
            extraSpace = extraSpace,
            childCount = children.size,
            hasWeightedChildren = hasWeightedChildren,
        )

        var currentLeading = arrangementMetrics.leadingSpace
        return buildList {
            children.forEachIndexed { index, child ->
                currentLeading += child.leadingMargin
                val trailing = currentLeading + child.size
                add(
                    LinearChildPlacement(
                        leading = currentLeading,
                        trailing = trailing,
                    ),
                )
                currentLeading = trailing + child.trailingMargin
                if (index != children.lastIndex) {
                    currentLeading += arrangementMetrics.gap
                }
            }
        }
    }
}
