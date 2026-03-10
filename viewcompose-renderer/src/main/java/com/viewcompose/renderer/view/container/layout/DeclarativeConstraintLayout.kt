package com.viewcompose.renderer.view.container

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.viewcompose.renderer.R
import com.viewcompose.renderer.view.tree.LayoutPassTracker
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
import com.viewcompose.ui.node.spec.ConstraintSetSpec

internal class DeclarativeConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    companion object {
        private const val WARNING_TAG = "UIConstraintLayout"
    }

    var inlineHelpersSpec: ConstraintHelpersSpec = ConstraintHelpersSpec()
        set(value) {
            field = value
            requestConstraintRebuild()
        }

    var decoupledConstraintSetSpec: ConstraintSetSpec? = null
        set(value) {
            field = value
            requestConstraintRebuild()
        }

    private val referenceIdToViewId = mutableMapOf<String, Int>()
    private val helperIdToViewId = mutableMapOf<String, Int>()
    private val emittedWarnings = mutableSetOf<String>()
    private var pendingConstraintRebuild = false

    init {
        clipChildren = false
        clipToPadding = false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val startNs = System.nanoTime()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LayoutPassTracker.recordMeasure(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onLayout(
        changed: Boolean,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
    ) {
        val startNs = System.nanoTime()
        super.onLayout(changed, left, top, right, bottom)
        LayoutPassTracker.recordLayout(
            viewName = javaClass.simpleName,
            durationNs = System.nanoTime() - startNs,
        )
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        requestConstraintRebuild()
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        requestConstraintRebuild()
    }

    fun requestConstraintRebuild() {
        if (pendingConstraintRebuild) {
            return
        }
        pendingConstraintRebuild = true
        post {
            pendingConstraintRebuild = false
            applyConstraintsInternal()
        }
    }

    fun applyConstraintsNow() {
        pendingConstraintRebuild = false
        applyConstraintsInternal()
    }

    private fun applyConstraintsInternal() {
        val inlineConstraints = collectInlineConstraints()
        val mergedConstraints = mergeConstraints(
            decoupled = decoupledConstraintSetSpec?.constraints.orEmpty(),
            inline = inlineConstraints,
        )
        if (hasCircularDependency(mergedConstraints)) {
            warnOnce(
                "Constraint graph contains circular references. Layout falls back to ConstraintLayout runtime resolution.",
            )
        }
        val mergedHelpers = mergeHelpers(
            decoupled = decoupledConstraintSetSpec?.helpers,
            inline = inlineHelpersSpec,
        )
        syncReferenceViewIds()
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        clearChildConstraints(constraintSet)
        val helperReferenceIds = applyGuidelinesAndBarriers(
            constraintSet = constraintSet,
            helpers = mergedHelpers,
        )
        applyChains(
            constraintSet = constraintSet,
            chains = mergedHelpers.chains,
            helperReferenceIds = helperReferenceIds,
        )
        applyItemConstraints(
            constraintSet = constraintSet,
            constraints = mergedConstraints,
            helperReferenceIds = helperReferenceIds,
        )
        try {
            constraintSet.applyTo(this)
        } catch (error: Throwable) {
            warnOnce("ConstraintSet apply failed: ${error.message}")
        }
    }

    private fun collectInlineConstraints(): Map<String, ConstraintItemSpec> {
        val constraints = linkedMapOf<String, ConstraintItemSpec>()
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            val referenceId = child.getTag(R.id.ui_framework_constraint_layout_id) as? String
            val inlineSpec = child.getTag(R.id.ui_framework_constraint_item_spec) as? ConstraintItemSpec
            if (referenceId == null || inlineSpec == null) {
                continue
            }
            val previous = constraints.put(referenceId, inlineSpec)
            if (previous != null && previous != inlineSpec) {
                warnOnce("Duplicate inline constraint id '$referenceId'. Last child wins.")
            }
        }
        return constraints
    }

    private fun mergeConstraints(
        decoupled: Map<String, ConstraintItemSpec>,
        inline: Map<String, ConstraintItemSpec>,
    ): Map<String, ConstraintItemSpec> {
        val merged = linkedMapOf<String, ConstraintItemSpec>()
        merged.putAll(decoupled)
        inline.forEach { (id, spec) ->
            if (merged.containsKey(id) && decoupled.containsKey(id)) {
                warnOnce("Inline constraint overrides decoupled ConstraintSet item '$id'.")
            }
            merged[id] = spec
        }
        return merged
    }

    private fun mergeHelpers(
        decoupled: ConstraintHelpersSpec?,
        inline: ConstraintHelpersSpec,
    ): ConstraintHelpersSpec {
        val guidelineMap = linkedMapOf<String, ConstraintGuidelineSpec>()
        decoupled?.guidelines.orEmpty().forEach { spec ->
            guidelineMap[spec.id] = spec
        }
        inline.guidelines.forEach { spec ->
            if (guidelineMap.containsKey(spec.id)) {
                warnOnce("Inline guideline '${spec.id}' overrides decoupled helper with same id.")
            }
            guidelineMap[spec.id] = spec
        }

        val barrierMap = linkedMapOf<String, ConstraintBarrierSpec>()
        decoupled?.barriers.orEmpty().forEach { spec ->
            barrierMap[spec.id] = spec
        }
        inline.barriers.forEach { spec ->
            if (barrierMap.containsKey(spec.id)) {
                warnOnce("Inline barrier '${spec.id}' overrides decoupled helper with same id.")
            }
            barrierMap[spec.id] = spec
        }

        return ConstraintHelpersSpec(
            guidelines = guidelineMap.values.toList(),
            barriers = barrierMap.values.toList(),
            chains = decoupled?.chains.orEmpty() + inline.chains,
        )
    }

    private fun syncReferenceViewIds() {
        val activeReferenceIds = mutableSetOf<String>()
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            val referenceId = child.getTag(R.id.ui_framework_constraint_layout_id) as? String ?: continue
            activeReferenceIds += referenceId
            val resolvedViewId = referenceIdToViewId.getOrPut(referenceId) { View.generateViewId() }
            if (child.id != resolvedViewId) {
                child.id = resolvedViewId
            }
        }
        referenceIdToViewId.keys.retainAll(activeReferenceIds)
    }

    private fun clearChildConstraints(constraintSet: ConstraintSet) {
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.id != View.NO_ID) {
                constraintSet.clear(child.id)
            }
        }
    }

    private fun applyGuidelinesAndBarriers(
        constraintSet: ConstraintSet,
        helpers: ConstraintHelpersSpec,
    ): Map<String, Int> {
        val activeHelpers = mutableSetOf<String>()
        val helperReferenceIds = mutableMapOf<String, Int>()

        helpers.guidelines.forEach { guideline ->
            val helperId = helperIdToViewId.getOrPut("guideline:${guideline.id}") { View.generateViewId() }
            activeHelpers += "guideline:${guideline.id}"
            helperReferenceIds[guideline.id] = helperId
            applyGuideline(constraintSet, helperId, guideline)
        }

        helpers.barriers.forEach { barrier ->
            val helperId = helperIdToViewId.getOrPut("barrier:${barrier.id}") { View.generateViewId() }
            activeHelpers += "barrier:${barrier.id}"
            helperReferenceIds[barrier.id] = helperId
            val references = barrier.referencedIds.mapNotNull { referenceId ->
                resolveReferenceId(
                    referenceId = referenceId,
                    helperReferenceIds = helperReferenceIds,
                    warningPrefix = "Barrier '${barrier.id}'",
                )
            }
            if (references.isEmpty()) {
                warnOnce("Barrier '${barrier.id}' has no valid referenced ids and will be ignored.")
                return@forEach
            }
            constraintSet.createBarrier(
                helperId,
                barrier.direction.toConstraintSetDirection(),
                barrier.margin,
                *references.toIntArray(),
            )
            if (!barrier.allowsGoneWidgets) {
                warnOnce(
                    "Barrier '${barrier.id}' sets allowsGoneWidgets=false, but this ConstraintSet API level does not expose direct toggling. Default behavior is used.",
                )
            }
        }

        helperIdToViewId.keys.retainAll(activeHelpers)
        return helperReferenceIds
    }

    private fun applyGuideline(
        constraintSet: ConstraintSet,
        helperId: Int,
        spec: ConstraintGuidelineSpec,
    ) {
        val orientation = when (spec.direction) {
            ConstraintGuidelineDirection.FromStart,
            ConstraintGuidelineDirection.FromEnd,
            -> ConstraintSet.VERTICAL_GUIDELINE

            ConstraintGuidelineDirection.FromTop,
            ConstraintGuidelineDirection.FromBottom,
            -> ConstraintSet.HORIZONTAL_GUIDELINE
        }
        constraintSet.create(helperId, orientation)
        when (val position = spec.position) {
            is ConstraintGuidelinePosition.Offset -> {
                when (spec.direction) {
                    ConstraintGuidelineDirection.FromStart,
                    ConstraintGuidelineDirection.FromTop,
                    -> constraintSet.setGuidelineBegin(helperId, position.value)

                    ConstraintGuidelineDirection.FromEnd,
                    ConstraintGuidelineDirection.FromBottom,
                    -> constraintSet.setGuidelineEnd(helperId, position.value)
                }
            }

            is ConstraintGuidelinePosition.Fraction -> {
                val percent = when (spec.direction) {
                    ConstraintGuidelineDirection.FromStart,
                    ConstraintGuidelineDirection.FromTop,
                    -> position.value

                    ConstraintGuidelineDirection.FromEnd,
                    ConstraintGuidelineDirection.FromBottom,
                    -> 1f - position.value
                }
                constraintSet.setGuidelinePercent(helperId, percent.coerceIn(0f, 1f))
            }
        }
    }

    private fun applyChains(
        constraintSet: ConstraintSet,
        chains: List<ConstraintChainSpec>,
        helperReferenceIds: Map<String, Int>,
    ) {
        chains.forEachIndexed { index, chain ->
            val resolvedIds = chain.referencedIds.mapNotNull { referenceId ->
                resolveReferenceId(
                    referenceId = referenceId,
                    helperReferenceIds = helperReferenceIds,
                    warningPrefix = "Chain[$index]",
                )
            }
            if (resolvedIds.size < 2) {
                warnOnce("Chain[$index] requires at least two valid references.")
                return@forEachIndexed
            }
            when (chain.orientation) {
                ConstraintChainOrientation.Horizontal -> {
                    applyHorizontalChain(constraintSet, resolvedIds, chain)
                }

                ConstraintChainOrientation.Vertical -> {
                    applyVerticalChain(constraintSet, resolvedIds, chain)
                }
            }
        }
    }

    private fun applyHorizontalChain(
        constraintSet: ConstraintSet,
        resolvedIds: List<Int>,
        chain: ConstraintChainSpec,
    ) {
        val first = resolvedIds.first()
        val last = resolvedIds.last()
        constraintSet.connect(first, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(last, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        for (index in resolvedIds.indices) {
            val viewId = resolvedIds[index]
            if (index > 0) {
                constraintSet.connect(
                    viewId,
                    ConstraintSet.START,
                    resolvedIds[index - 1],
                    ConstraintSet.END,
                )
            }
            if (index < resolvedIds.lastIndex) {
                constraintSet.connect(
                    viewId,
                    ConstraintSet.END,
                    resolvedIds[index + 1],
                    ConstraintSet.START,
                )
            }
        }
        constraintSet.setHorizontalChainStyle(first, chain.style.toConstraintSetChainStyle())
        chain.bias?.let { bias ->
            constraintSet.setHorizontalBias(first, bias)
        }
    }

    private fun applyVerticalChain(
        constraintSet: ConstraintSet,
        resolvedIds: List<Int>,
        chain: ConstraintChainSpec,
    ) {
        val first = resolvedIds.first()
        val last = resolvedIds.last()
        constraintSet.connect(first, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.connect(last, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
        for (index in resolvedIds.indices) {
            val viewId = resolvedIds[index]
            if (index > 0) {
                constraintSet.connect(
                    viewId,
                    ConstraintSet.TOP,
                    resolvedIds[index - 1],
                    ConstraintSet.BOTTOM,
                )
            }
            if (index < resolvedIds.lastIndex) {
                constraintSet.connect(
                    viewId,
                    ConstraintSet.BOTTOM,
                    resolvedIds[index + 1],
                    ConstraintSet.TOP,
                )
            }
        }
        constraintSet.setVerticalChainStyle(first, chain.style.toConstraintSetChainStyle())
        chain.bias?.let { bias ->
            constraintSet.setVerticalBias(first, bias)
        }
    }

    private fun applyItemConstraints(
        constraintSet: ConstraintSet,
        constraints: Map<String, ConstraintItemSpec>,
        helperReferenceIds: Map<String, Int>,
    ) {
        constraints.forEach { (referenceId, item) ->
            val viewId = referenceIdToViewId[referenceId]
            if (viewId == null) {
                warnOnce("Constraint item '$referenceId' has no matching child layoutId.")
                return@forEach
            }
            constraintSet.constrainWidth(viewId, item.width.toLayoutParam())
            constraintSet.constrainHeight(viewId, item.height.toLayoutParam())
            item.start?.applyTo(
                constraintSet = constraintSet,
                sourceViewId = viewId,
                sourceAnchor = ConstraintAnchor.Start,
                helperReferenceIds = helperReferenceIds,
                referenceId = referenceId,
            )
            item.end?.applyTo(
                constraintSet = constraintSet,
                sourceViewId = viewId,
                sourceAnchor = ConstraintAnchor.End,
                helperReferenceIds = helperReferenceIds,
                referenceId = referenceId,
            )
            item.top?.applyTo(
                constraintSet = constraintSet,
                sourceViewId = viewId,
                sourceAnchor = ConstraintAnchor.Top,
                helperReferenceIds = helperReferenceIds,
                referenceId = referenceId,
            )
            item.bottom?.applyTo(
                constraintSet = constraintSet,
                sourceViewId = viewId,
                sourceAnchor = ConstraintAnchor.Bottom,
                helperReferenceIds = helperReferenceIds,
                referenceId = referenceId,
            )
            item.baseline?.let { baselineTarget ->
                if (baselineTarget.anchor != ConstraintAnchor.Baseline) {
                    warnOnce("Constraint item '$referenceId' baseline target must use Baseline anchor.")
                } else {
                    val targetId = resolveTargetId(
                        target = baselineTarget,
                        helperReferenceIds = helperReferenceIds,
                    )
                    if (targetId != null) {
                        constraintSet.connect(
                            viewId,
                            ConstraintSet.BASELINE,
                            targetId,
                            ConstraintSet.BASELINE,
                        )
                    } else {
                        warnOnce("Constraint item '$referenceId' baseline target '${baselineTarget.id}' was not found.")
                    }
                }
            }
            item.horizontalBias?.let { bias -> constraintSet.setHorizontalBias(viewId, bias) }
            item.verticalBias?.let { bias -> constraintSet.setVerticalBias(viewId, bias) }
            item.dimensionRatio?.let { ratio -> constraintSet.setDimensionRatio(viewId, ratio) }
        }
    }

    private fun ConstraintAnchorLink.applyTo(
        constraintSet: ConstraintSet,
        sourceViewId: Int,
        sourceAnchor: ConstraintAnchor,
        helperReferenceIds: Map<String, Int>,
        referenceId: String,
    ) {
        val targetId = resolveTargetId(
            target = target,
            helperReferenceIds = helperReferenceIds,
        )
        if (targetId == null) {
            warnOnce("Constraint item '$referenceId' target '${target.id}' was not found.")
            return
        }
        val sourceSide = sourceAnchor.toConstraintSetSide()
        val targetSide = target.anchor.toConstraintSetSide()
        if (sourceAnchor == ConstraintAnchor.Baseline && target.anchor != ConstraintAnchor.Baseline) {
            warnOnce("Constraint item '$referenceId' baseline can only connect to baseline.")
            return
        }
        constraintSet.connect(
            sourceViewId,
            sourceSide,
            targetId,
            targetSide,
            margin,
        )
        goneMargin?.let { marginValue ->
            constraintSet.setGoneMargin(
                sourceViewId,
                sourceSide,
                marginValue,
            )
        }
    }

    private fun resolveTargetId(
        target: ConstraintAnchorTarget,
        helperReferenceIds: Map<String, Int>,
    ): Int? {
        val targetRef = target.id ?: return ConstraintSet.PARENT_ID
        return referenceIdToViewId[targetRef] ?: helperReferenceIds[targetRef]
    }

    private fun resolveReferenceId(
        referenceId: String,
        helperReferenceIds: Map<String, Int>,
        warningPrefix: String,
    ): Int? {
        val target = referenceIdToViewId[referenceId] ?: helperReferenceIds[referenceId]
        if (target == null) {
            warnOnce("$warningPrefix references missing id '$referenceId'.")
        }
        return target
    }

    private fun hasCircularDependency(constraints: Map<String, ConstraintItemSpec>): Boolean {
        val graph = mutableMapOf<String, MutableSet<String>>()
        constraints.forEach { (sourceId, spec) ->
            val dependencies = mutableSetOf<String>()
            listOf(
                spec.start?.target,
                spec.end?.target,
                spec.top?.target,
                spec.bottom?.target,
                spec.baseline,
            ).forEach { target ->
                val targetId = target?.id ?: return@forEach
                if (constraints.containsKey(targetId)) {
                    dependencies += targetId
                }
            }
            graph[sourceId] = dependencies
        }
        val visitState = mutableMapOf<String, Int>() // 0=unvisited, 1=visiting, 2=visited
        fun dfs(node: String): Boolean {
            when (visitState[node]) {
                1 -> return true
                2 -> return false
            }
            visitState[node] = 1
            for (next in graph[node].orEmpty()) {
                if (dfs(next)) {
                    return true
                }
            }
            visitState[node] = 2
            return false
        }
        return graph.keys.any { node -> dfs(node) }
    }

    private fun ConstraintAnchor.toConstraintSetSide(): Int {
        return when (this) {
            ConstraintAnchor.Start -> ConstraintSet.START
            ConstraintAnchor.End -> ConstraintSet.END
            ConstraintAnchor.Top -> ConstraintSet.TOP
            ConstraintAnchor.Bottom -> ConstraintSet.BOTTOM
            ConstraintAnchor.Baseline -> ConstraintSet.BASELINE
        }
    }

    private fun ConstraintDimension.toLayoutParam(): Int {
        return when (this) {
            ConstraintDimension.WrapContent -> LayoutParams.WRAP_CONTENT
            ConstraintDimension.FillToConstraints -> 0
            ConstraintDimension.MatchParent -> LayoutParams.MATCH_PARENT
            is ConstraintDimension.Fixed -> value
        }
    }

    private fun ConstraintBarrierDirection.toConstraintSetDirection(): Int {
        return when (this) {
            ConstraintBarrierDirection.Start -> ConstraintSet.START
            ConstraintBarrierDirection.End -> ConstraintSet.END
            ConstraintBarrierDirection.Top -> ConstraintSet.TOP
            ConstraintBarrierDirection.Bottom -> ConstraintSet.BOTTOM
        }
    }

    private fun ConstraintChainStyle.toConstraintSetChainStyle(): Int {
        return when (this) {
            ConstraintChainStyle.Spread -> ConstraintSet.CHAIN_SPREAD
            ConstraintChainStyle.SpreadInside -> ConstraintSet.CHAIN_SPREAD_INSIDE
            ConstraintChainStyle.Packed -> ConstraintSet.CHAIN_PACKED
        }
    }

    private fun warnOnce(message: String) {
        if (emittedWarnings.add(message)) {
            Log.w(WARNING_TAG, message)
        }
    }
}
