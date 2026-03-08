package com.viewcompose.renderer.layout

import com.viewcompose.ui.layout.MainAxisArrangement

internal object LinearArrangementCalculator {
    fun calculate(
        arrangement: MainAxisArrangement,
        itemSpacing: Int,
        extraSpace: Int,
        childCount: Int,
        hasWeightedChildren: Boolean,
    ): ArrangementMetrics {
        if (childCount <= 1) {
            return ArrangementMetrics(
                leadingSpace = when (arrangement) {
                    MainAxisArrangement.Center -> extraSpace / 2
                    MainAxisArrangement.End -> extraSpace
                    MainAxisArrangement.SpaceAround,
                    MainAxisArrangement.SpaceEvenly,
                    -> extraSpace / 2
                    MainAxisArrangement.Start,
                    MainAxisArrangement.SpaceBetween,
                    -> 0
                },
                gap = itemSpacing,
            )
        }

        if (hasWeightedChildren) {
            return when (arrangement) {
                MainAxisArrangement.Start,
                MainAxisArrangement.SpaceBetween,
                MainAxisArrangement.SpaceAround,
                MainAxisArrangement.SpaceEvenly,
                -> ArrangementMetrics(
                    leadingSpace = 0,
                    gap = itemSpacing,
                )

                MainAxisArrangement.Center -> ArrangementMetrics(
                    leadingSpace = extraSpace / 2,
                    gap = itemSpacing,
                )

                MainAxisArrangement.End -> ArrangementMetrics(
                    leadingSpace = extraSpace,
                    gap = itemSpacing,
                )
            }
        }

        return when (arrangement) {
            MainAxisArrangement.Start -> ArrangementMetrics(
                leadingSpace = 0,
                gap = itemSpacing,
            )
            MainAxisArrangement.Center -> ArrangementMetrics(
                leadingSpace = extraSpace / 2,
                gap = itemSpacing,
            )
            MainAxisArrangement.End -> ArrangementMetrics(
                leadingSpace = extraSpace,
                gap = itemSpacing,
            )
            MainAxisArrangement.SpaceBetween -> ArrangementMetrics(
                leadingSpace = 0,
                gap = itemSpacing + extraSpace / (childCount - 1),
            )
            MainAxisArrangement.SpaceAround -> {
                val unit = extraSpace / (childCount * 2)
                ArrangementMetrics(
                    leadingSpace = unit,
                    gap = itemSpacing + unit * 2,
                )
            }
            MainAxisArrangement.SpaceEvenly -> {
                val unit = extraSpace / (childCount + 1)
                ArrangementMetrics(
                    leadingSpace = unit,
                    gap = itemSpacing + unit,
                )
            }
        }
    }
}

internal data class ArrangementMetrics(
    val leadingSpace: Int,
    val gap: Int,
)
