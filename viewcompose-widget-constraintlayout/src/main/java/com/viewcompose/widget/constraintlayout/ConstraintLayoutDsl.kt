package com.viewcompose.widget.constraintlayout

import com.viewcompose.ui.modifier.ConstraintModifierElement
import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.layoutId
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.spec.ConstraintAnchor
import com.viewcompose.ui.node.spec.ConstraintAnchorLink
import com.viewcompose.ui.node.spec.ConstraintAnchorTarget
import com.viewcompose.ui.node.spec.ConstraintBarrierDirection
import com.viewcompose.ui.node.spec.ConstraintBarrierSpec
import com.viewcompose.ui.node.spec.ConstraintChainOrientation
import com.viewcompose.ui.node.spec.ConstraintChainSpec
import com.viewcompose.ui.node.spec.ConstraintChainStyle
import com.viewcompose.ui.node.spec.ConstraintDimension
import com.viewcompose.ui.node.spec.ConstraintGuidelineDirection
import com.viewcompose.ui.node.spec.ConstraintGuidelinePosition
import com.viewcompose.ui.node.spec.ConstraintGuidelineSpec
import com.viewcompose.ui.node.spec.ConstraintHelpersSpec
import com.viewcompose.ui.node.spec.ConstraintItemSpec
import com.viewcompose.ui.node.spec.ConstraintLayoutNodeProps
import com.viewcompose.ui.node.spec.ConstraintSetSpec
import com.viewcompose.widget.core.UiTreeBuilder

typealias ConstraintLayoutScope = UiTreeBuilder

data class ConstraintReference(
    override val id: String,
) : ConstraintReferenceTarget

sealed interface ConstraintReferenceTarget {
    val id: String?
}

data object ConstraintParentReference : ConstraintReferenceTarget {
    override val id: String? = null
}

val parent: ConstraintReferenceTarget
    get() = ConstraintParentReference

private class MutableConstraintHelpersCollector {
    private var nextAutoId = 0
    val guidelines = mutableListOf<ConstraintGuidelineSpec>()
    val barriers = mutableListOf<ConstraintBarrierSpec>()
    val chains = mutableListOf<ConstraintChainSpec>()

    fun allocId(prefix: String): String {
        val id = "$prefix-${nextAutoId}"
        nextAutoId += 1
        return id
    }

    fun toSpec(): ConstraintHelpersSpec {
        return ConstraintHelpersSpec(
            guidelines = guidelines,
            barriers = barriers,
            chains = chains,
        )
    }
}

private class ConstraintLayoutDslContext(
    val helpers: MutableConstraintHelpersCollector,
)

private object ConstraintLayoutDslContextStack {
    private val threadLocal: ThreadLocal<ArrayDeque<ConstraintLayoutDslContext>> = ThreadLocal.withInitial {
        ArrayDeque<ConstraintLayoutDslContext>()
    }

    private fun deque(): ArrayDeque<ConstraintLayoutDslContext> {
        return requireNotNull(threadLocal.get()) {
            "ConstraintLayout DSL context stack is unexpectedly unavailable."
        }
    }

    fun push(context: ConstraintLayoutDslContext) {
        deque().addLast(context)
    }

    fun pop() {
        val currentDeque = deque()
        if (currentDeque.isNotEmpty()) {
            currentDeque.removeLast()
        }
    }

    fun current(): ConstraintLayoutDslContext? = deque().lastOrNull()
}

private fun requireConstraintContext(): ConstraintLayoutDslContext {
    return requireNotNull(ConstraintLayoutDslContextStack.current()) {
        "ConstraintLayout helper APIs can only be called inside ConstraintLayout { ... }."
    }
}

private fun ConstraintReferenceTarget.toAnchorTarget(anchor: ConstraintAnchor): ConstraintAnchorTarget {
    return ConstraintAnchorTarget(
        id = id,
        anchor = anchor,
    )
}

class ConstraintConstrainScope internal constructor() {
    private var start: ConstraintAnchorLink? = null
    private var end: ConstraintAnchorLink? = null
    private var top: ConstraintAnchorLink? = null
    private var bottom: ConstraintAnchorLink? = null
    private var baseline: ConstraintAnchorTarget? = null
    var width: ConstraintDimension = ConstraintDimension.WrapContent
    var height: ConstraintDimension = ConstraintDimension.WrapContent
    var horizontalBias: Float? = null
    var verticalBias: Float? = null
    var dimensionRatio: String? = null

    fun startToStart(
        target: ConstraintReferenceTarget = parent,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        start = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Start),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun startToEnd(
        target: ConstraintReferenceTarget,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        start = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.End),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun endToStart(
        target: ConstraintReferenceTarget,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        end = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Start),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun endToEnd(
        target: ConstraintReferenceTarget = parent,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        end = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.End),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun topToTop(
        target: ConstraintReferenceTarget = parent,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        top = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Top),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun topToBottom(
        target: ConstraintReferenceTarget,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        top = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Bottom),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun bottomToTop(
        target: ConstraintReferenceTarget,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        bottom = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Top),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun bottomToBottom(
        target: ConstraintReferenceTarget = parent,
        margin: Int = 0,
        goneMargin: Int? = null,
    ) {
        bottom = ConstraintAnchorLink(
            target = target.toAnchorTarget(ConstraintAnchor.Bottom),
            margin = margin,
            goneMargin = goneMargin,
        )
    }

    fun baselineToBaseline(target: ConstraintReference) {
        baseline = ConstraintAnchorTarget.ref(
            id = target.id,
            anchor = ConstraintAnchor.Baseline,
        )
    }

    fun centerHorizontallyTo(target: ConstraintReferenceTarget = parent) {
        startToStart(target)
        endToEnd(target)
    }

    fun centerVerticallyTo(target: ConstraintReferenceTarget = parent) {
        topToTop(target)
        bottomToBottom(target)
    }

    internal fun build(): ConstraintItemSpec {
        return ConstraintItemSpec(
            start = start,
            end = end,
            top = top,
            bottom = bottom,
            baseline = baseline,
            width = width,
            height = height,
            horizontalBias = horizontalBias,
            verticalBias = verticalBias,
            dimensionRatio = dimensionRatio,
        )
    }
}

private fun buildConstraintSpec(content: ConstraintConstrainScope.() -> Unit): ConstraintItemSpec {
    return ConstraintConstrainScope()
        .apply(content)
        .build()
}

fun Modifier.constrainAs(
    ref: ConstraintReference,
    content: ConstraintConstrainScope.() -> Unit,
): Modifier {
    return this
        .layoutId(ref.id)
        .then(
            ConstraintModifierElement(
                constraint = buildConstraintSpec(content),
                referenceId = ref.id,
            ),
        )
}

fun Modifier.constrain(
    id: String,
    content: ConstraintConstrainScope.() -> Unit,
): Modifier {
    return this
        .layoutId(id)
        .then(
            ConstraintModifierElement(
                constraint = buildConstraintSpec(content),
                referenceId = id,
            ),
        )
}

fun UiTreeBuilder.ConstraintLayout(
    key: Any? = null,
    constraintSet: ConstraintSetSpec? = null,
    modifier: Modifier = Modifier,
    content: ConstraintLayoutScope.() -> Unit,
) {
    val context = ConstraintLayoutDslContext(
        helpers = MutableConstraintHelpersCollector(),
    )
    ConstraintLayoutDslContextStack.push(context)
    try {
        emit(
            type = NodeType.ConstraintLayout,
            key = key,
            spec = ConstraintLayoutNodeProps(
                constraintSet = constraintSet,
                helpers = context.helpers.toSpec(),
            ),
            modifier = modifier,
            content = content,
        )
    } finally {
        ConstraintLayoutDslContextStack.pop()
    }
}

fun ConstraintLayoutScope.createRef(id: String): ConstraintReference {
    return ConstraintReference(id = id)
}

fun ConstraintLayoutScope.createRefs(vararg ids: String): Array<ConstraintReference> {
    return ids.map { id -> createRef(id) }.toTypedArray()
}

private fun ConstraintLayoutScope.allocHelperId(prefix: String): String {
    return requireConstraintContext().helpers.allocId(prefix)
}

fun ConstraintLayoutScope.createGuidelineFromStart(
    offset: Int,
    id: String = allocHelperId("guideline-start"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromStart,
        position = ConstraintGuidelinePosition.Offset(offset),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromStart(
    fraction: Float,
    id: String = allocHelperId("guideline-start"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromStart,
        position = ConstraintGuidelinePosition.Fraction(fraction),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromEnd(
    offset: Int,
    id: String = allocHelperId("guideline-end"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromEnd,
        position = ConstraintGuidelinePosition.Offset(offset),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromEnd(
    fraction: Float,
    id: String = allocHelperId("guideline-end"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromEnd,
        position = ConstraintGuidelinePosition.Fraction(fraction),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromTop(
    offset: Int,
    id: String = allocHelperId("guideline-top"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromTop,
        position = ConstraintGuidelinePosition.Offset(offset),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromTop(
    fraction: Float,
    id: String = allocHelperId("guideline-top"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromTop,
        position = ConstraintGuidelinePosition.Fraction(fraction),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromBottom(
    offset: Int,
    id: String = allocHelperId("guideline-bottom"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromBottom,
        position = ConstraintGuidelinePosition.Offset(offset),
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createGuidelineFromBottom(
    fraction: Float,
    id: String = allocHelperId("guideline-bottom"),
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.guidelines += ConstraintGuidelineSpec(
        id = id,
        direction = ConstraintGuidelineDirection.FromBottom,
        position = ConstraintGuidelinePosition.Fraction(fraction),
    )
    return ConstraintReference(id)
}

private fun ConstraintLayoutScope.registerBarrier(
    id: String,
    direction: ConstraintBarrierDirection,
    refs: Array<out ConstraintReference>,
    margin: Int,
    allowsGoneWidgets: Boolean,
): ConstraintReference {
    val context = requireConstraintContext()
    context.helpers.barriers += ConstraintBarrierSpec(
        id = id,
        direction = direction,
        referencedIds = refs.map { ref -> ref.id },
        margin = margin,
        allowsGoneWidgets = allowsGoneWidgets,
    )
    return ConstraintReference(id)
}

fun ConstraintLayoutScope.createStartBarrier(
    vararg refs: ConstraintReference,
    id: String = allocHelperId("barrier-start"),
    margin: Int = 0,
    allowsGoneWidgets: Boolean = true,
): ConstraintReference {
    return registerBarrier(id, ConstraintBarrierDirection.Start, refs, margin, allowsGoneWidgets)
}

fun ConstraintLayoutScope.createEndBarrier(
    vararg refs: ConstraintReference,
    id: String = allocHelperId("barrier-end"),
    margin: Int = 0,
    allowsGoneWidgets: Boolean = true,
): ConstraintReference {
    return registerBarrier(id, ConstraintBarrierDirection.End, refs, margin, allowsGoneWidgets)
}

fun ConstraintLayoutScope.createTopBarrier(
    vararg refs: ConstraintReference,
    id: String = allocHelperId("barrier-top"),
    margin: Int = 0,
    allowsGoneWidgets: Boolean = true,
): ConstraintReference {
    return registerBarrier(id, ConstraintBarrierDirection.Top, refs, margin, allowsGoneWidgets)
}

fun ConstraintLayoutScope.createBottomBarrier(
    vararg refs: ConstraintReference,
    id: String = allocHelperId("barrier-bottom"),
    margin: Int = 0,
    allowsGoneWidgets: Boolean = true,
): ConstraintReference {
    return registerBarrier(id, ConstraintBarrierDirection.Bottom, refs, margin, allowsGoneWidgets)
}

private fun ConstraintLayoutScope.registerChain(
    orientation: ConstraintChainOrientation,
    refs: Array<out ConstraintReference>,
    style: ConstraintChainStyle,
    bias: Float?,
) {
    val context = requireConstraintContext()
    context.helpers.chains += ConstraintChainSpec(
        orientation = orientation,
        referencedIds = refs.map { ref -> ref.id },
        style = style,
        bias = bias,
    )
}

fun ConstraintLayoutScope.createHorizontalChain(
    vararg refs: ConstraintReference,
    style: ConstraintChainStyle = ConstraintChainStyle.Spread,
    bias: Float? = null,
) {
    registerChain(
        orientation = ConstraintChainOrientation.Horizontal,
        refs = refs,
        style = style,
        bias = bias,
    )
}

fun ConstraintLayoutScope.createVerticalChain(
    vararg refs: ConstraintReference,
    style: ConstraintChainStyle = ConstraintChainStyle.Spread,
    bias: Float? = null,
) {
    registerChain(
        orientation = ConstraintChainOrientation.Vertical,
        refs = refs,
        style = style,
        bias = bias,
    )
}

class ConstraintSetBuilder internal constructor() {
    private val constraints = linkedMapOf<String, ConstraintItemSpec>()
    private val helpers = MutableConstraintHelpersCollector()

    fun createRef(id: String): ConstraintReference {
        return ConstraintReference(id = id)
    }

    fun createRefs(vararg ids: String): Array<ConstraintReference> {
        return ids.map { id -> createRef(id) }.toTypedArray()
    }

    fun constrain(
        id: String,
        content: ConstraintConstrainScope.() -> Unit,
    ) {
        constraints[id] = buildConstraintSpec(content)
    }

    fun createGuidelineFromStart(
        offset: Int,
        id: String = helpers.allocId("guideline-start"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromStart,
            position = ConstraintGuidelinePosition.Offset(offset),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromStart(
        fraction: Float,
        id: String = helpers.allocId("guideline-start"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromStart,
            position = ConstraintGuidelinePosition.Fraction(fraction),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromEnd(
        offset: Int,
        id: String = helpers.allocId("guideline-end"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromEnd,
            position = ConstraintGuidelinePosition.Offset(offset),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromEnd(
        fraction: Float,
        id: String = helpers.allocId("guideline-end"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromEnd,
            position = ConstraintGuidelinePosition.Fraction(fraction),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromTop(
        offset: Int,
        id: String = helpers.allocId("guideline-top"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromTop,
            position = ConstraintGuidelinePosition.Offset(offset),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromTop(
        fraction: Float,
        id: String = helpers.allocId("guideline-top"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromTop,
            position = ConstraintGuidelinePosition.Fraction(fraction),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromBottom(
        offset: Int,
        id: String = helpers.allocId("guideline-bottom"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromBottom,
            position = ConstraintGuidelinePosition.Offset(offset),
        )
        return ConstraintReference(id)
    }

    fun createGuidelineFromBottom(
        fraction: Float,
        id: String = helpers.allocId("guideline-bottom"),
    ): ConstraintReference {
        helpers.guidelines += ConstraintGuidelineSpec(
            id = id,
            direction = ConstraintGuidelineDirection.FromBottom,
            position = ConstraintGuidelinePosition.Fraction(fraction),
        )
        return ConstraintReference(id)
    }

    fun createStartBarrier(
        vararg refs: ConstraintReference,
        id: String = helpers.allocId("barrier-start"),
        margin: Int = 0,
        allowsGoneWidgets: Boolean = true,
    ): ConstraintReference {
        helpers.barriers += ConstraintBarrierSpec(
            id = id,
            direction = ConstraintBarrierDirection.Start,
            referencedIds = refs.map { ref -> ref.id },
            margin = margin,
            allowsGoneWidgets = allowsGoneWidgets,
        )
        return ConstraintReference(id)
    }

    fun createEndBarrier(
        vararg refs: ConstraintReference,
        id: String = helpers.allocId("barrier-end"),
        margin: Int = 0,
        allowsGoneWidgets: Boolean = true,
    ): ConstraintReference {
        helpers.barriers += ConstraintBarrierSpec(
            id = id,
            direction = ConstraintBarrierDirection.End,
            referencedIds = refs.map { ref -> ref.id },
            margin = margin,
            allowsGoneWidgets = allowsGoneWidgets,
        )
        return ConstraintReference(id)
    }

    fun createTopBarrier(
        vararg refs: ConstraintReference,
        id: String = helpers.allocId("barrier-top"),
        margin: Int = 0,
        allowsGoneWidgets: Boolean = true,
    ): ConstraintReference {
        helpers.barriers += ConstraintBarrierSpec(
            id = id,
            direction = ConstraintBarrierDirection.Top,
            referencedIds = refs.map { ref -> ref.id },
            margin = margin,
            allowsGoneWidgets = allowsGoneWidgets,
        )
        return ConstraintReference(id)
    }

    fun createBottomBarrier(
        vararg refs: ConstraintReference,
        id: String = helpers.allocId("barrier-bottom"),
        margin: Int = 0,
        allowsGoneWidgets: Boolean = true,
    ): ConstraintReference {
        helpers.barriers += ConstraintBarrierSpec(
            id = id,
            direction = ConstraintBarrierDirection.Bottom,
            referencedIds = refs.map { ref -> ref.id },
            margin = margin,
            allowsGoneWidgets = allowsGoneWidgets,
        )
        return ConstraintReference(id)
    }

    fun createHorizontalChain(
        vararg refs: ConstraintReference,
        style: ConstraintChainStyle = ConstraintChainStyle.Spread,
        bias: Float? = null,
    ) {
        helpers.chains += ConstraintChainSpec(
            orientation = ConstraintChainOrientation.Horizontal,
            referencedIds = refs.map { ref -> ref.id },
            style = style,
            bias = bias,
        )
    }

    fun createVerticalChain(
        vararg refs: ConstraintReference,
        style: ConstraintChainStyle = ConstraintChainStyle.Spread,
        bias: Float? = null,
    ) {
        helpers.chains += ConstraintChainSpec(
            orientation = ConstraintChainOrientation.Vertical,
            referencedIds = refs.map { ref -> ref.id },
            style = style,
            bias = bias,
        )
    }

    internal fun build(): ConstraintSetSpec {
        return ConstraintSetSpec(
            constraints = constraints.toMap(),
            helpers = helpers.toSpec(),
        )
    }
}

fun constraintSet(content: ConstraintSetBuilder.() -> Unit): ConstraintSetSpec {
    return ConstraintSetBuilder()
        .apply(content)
        .build()
}
