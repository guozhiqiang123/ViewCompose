package com.viewcompose.ui.node.spec

data class ConstraintLayoutNodeProps(
    val constraintSet: ConstraintSetSpec? = null,
    val helpers: ConstraintHelpersSpec = ConstraintHelpersSpec(),
) : NodeSpec

data class ConstraintSetSpec(
    val constraints: Map<String, ConstraintItemSpec> = emptyMap(),
    val helpers: ConstraintHelpersSpec = ConstraintHelpersSpec(),
)

data class ConstraintHelpersSpec(
    val guidelines: List<ConstraintGuidelineSpec> = emptyList(),
    val barriers: List<ConstraintBarrierSpec> = emptyList(),
    val chains: List<ConstraintChainSpec> = emptyList(),
)

data class ConstraintItemSpec(
    val start: ConstraintAnchorLink? = null,
    val end: ConstraintAnchorLink? = null,
    val top: ConstraintAnchorLink? = null,
    val bottom: ConstraintAnchorLink? = null,
    val baseline: ConstraintAnchorTarget? = null,
    val baselineToTop: ConstraintAnchorLink? = null,
    val baselineToBottom: ConstraintAnchorLink? = null,
    val width: ConstraintDimension = ConstraintDimension.WrapContent,
    val height: ConstraintDimension = ConstraintDimension.WrapContent,
    val widthMin: Int? = null,
    val widthMax: Int? = null,
    val widthPercent: Float? = null,
    val heightMin: Int? = null,
    val heightMax: Int? = null,
    val heightPercent: Float? = null,
    val constrainedWidth: Boolean = false,
    val constrainedHeight: Boolean = false,
    val horizontalBias: Float? = null,
    val verticalBias: Float? = null,
    val dimensionRatio: String? = null,
    val circle: ConstraintCircleSpec? = null,
)

data class ConstraintAnchorLink(
    val target: ConstraintAnchorTarget,
    val margin: Int = 0,
    val goneMargin: Int? = null,
)

data class ConstraintAnchorTarget(
    val id: String?,
    val anchor: ConstraintAnchor,
) {
    companion object {
        fun parent(anchor: ConstraintAnchor): ConstraintAnchorTarget = ConstraintAnchorTarget(
            id = null,
            anchor = anchor,
        )

        fun ref(
            id: String,
            anchor: ConstraintAnchor,
        ): ConstraintAnchorTarget = ConstraintAnchorTarget(
            id = id,
            anchor = anchor,
        )
    }
}

enum class ConstraintAnchor {
    Start,
    End,
    Top,
    Bottom,
    Baseline,
}

data class ConstraintCircleSpec(
    val targetId: String,
    val radius: Int,
    val angle: Float,
)

sealed interface ConstraintDimension {
    data object WrapContent : ConstraintDimension

    data object FillToConstraints : ConstraintDimension

    data object MatchParent : ConstraintDimension

    data class Fixed(
        val value: Int,
    ) : ConstraintDimension
}

data class ConstraintGuidelineSpec(
    val id: String,
    val direction: ConstraintGuidelineDirection,
    val position: ConstraintGuidelinePosition,
)

enum class ConstraintGuidelineDirection {
    FromStart,
    FromEnd,
    FromTop,
    FromBottom,
}

sealed interface ConstraintGuidelinePosition {
    data class Offset(
        val value: Int,
    ) : ConstraintGuidelinePosition

    data class Fraction(
        val value: Float,
    ) : ConstraintGuidelinePosition
}

data class ConstraintBarrierSpec(
    val id: String,
    val direction: ConstraintBarrierDirection,
    val referencedIds: List<String>,
    val margin: Int = 0,
    val allowsGoneWidgets: Boolean = true,
)

enum class ConstraintBarrierDirection {
    Start,
    End,
    Top,
    Bottom,
}

data class ConstraintChainSpec(
    val orientation: ConstraintChainOrientation,
    val referencedIds: List<String>,
    val weights: List<Float>? = null,
    val style: ConstraintChainStyle = ConstraintChainStyle.Spread,
    val bias: Float? = null,
)

enum class ConstraintChainOrientation {
    Horizontal,
    Vertical,
}

enum class ConstraintChainStyle {
    Spread,
    SpreadInside,
    Packed,
}
