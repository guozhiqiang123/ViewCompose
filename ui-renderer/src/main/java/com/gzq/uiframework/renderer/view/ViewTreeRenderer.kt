package com.gzq.uiframework.renderer.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.AlphaModifierElement
import com.gzq.uiframework.renderer.modifier.BackgroundColorModifierElement
import com.gzq.uiframework.renderer.modifier.BoxAlignModifierElement
import com.gzq.uiframework.renderer.modifier.ClickableModifierElement
import com.gzq.uiframework.renderer.modifier.HeightModifierElement
import com.gzq.uiframework.renderer.modifier.HorizontalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.MarginModifierElement
import com.gzq.uiframework.renderer.modifier.OffsetModifierElement
import com.gzq.uiframework.renderer.modifier.PaddingModifierElement
import com.gzq.uiframework.renderer.modifier.SizeModifierElement
import com.gzq.uiframework.renderer.modifier.TextColorModifierElement
import com.gzq.uiframework.renderer.modifier.TextSizeModifierElement
import com.gzq.uiframework.renderer.modifier.VerticalAlignModifierElement
import com.gzq.uiframework.renderer.modifier.Visibility
import com.gzq.uiframework.renderer.modifier.VisibilityModifierElement
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.WidthModifierElement
import com.gzq.uiframework.renderer.modifier.ZIndexModifierElement
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.reconcile.ChildReconciler
import com.gzq.uiframework.renderer.reconcile.InsertPatch
import com.gzq.uiframework.renderer.reconcile.ReconcileNode
import com.gzq.uiframework.renderer.reconcile.ReconcileResult
import com.gzq.uiframework.renderer.reconcile.RemovePatch
import com.gzq.uiframework.renderer.reconcile.RenderPatch
import com.gzq.uiframework.renderer.reconcile.ReusePatch
import com.gzq.uiframework.renderer.R

object ViewTreeRenderer {
    fun disposeMounted(
        container: ViewGroup,
        mountedNodes: List<MountedNode>,
    ) {
        mountedNodes.forEach { mountedNode ->
            disposeMountedNode(mountedNode)
            container.removeView(mountedNode.view)
        }
    }

    fun renderInto(
        container: ViewGroup,
        previous: List<MountedNode>,
        nodes: List<VNode>,
        onReconcile: ((ReconcileResult<MountedNode>) -> Unit)? = null,
    ): List<MountedNode> {
        val reconcileResult = ChildReconciler.reconcile(
            previous = previous.map { mountedNode ->
                ReconcileNode(
                    vnode = mountedNode.vnode,
                    payload = mountedNode,
                )
            },
            nodes = nodes,
        )
        onReconcile?.invoke(reconcileResult)
        val nextMounted = mutableListOf<MountedNode>()
        reconcileResult.patches.forEach { patch ->
            nextMounted += applyPatch(
                container = container,
                patch = patch,
            )
        }
        reconcileResult.removals.forEach { removal ->
            applyRemoval(
                container = container,
                removal = removal,
            )
        }
        return nextMounted
    }

    private fun applyPatch(
        container: ViewGroup,
        patch: RenderPatch<MountedNode>,
    ): MountedNode {
        return when (patch) {
            is InsertPatch -> {
                val mountedNode = mountNode(container.context, patch.nextVNode)
                container.addView(
                    mountedNode.view,
                    patch.targetIndex.coerceAtMost(container.childCount),
                    createLayoutParams(container, patch.nextVNode),
                )
                mountedNode
            }

            is ReusePatch -> {
                val mountedNode = patch.payload
                bindView(mountedNode.view, patch.nextVNode)
                mountedNode.view.layoutParams = createLayoutParams(container, patch.nextVNode)
                mountedNode.children = reconcileChildren(
                    view = mountedNode.view,
                    previousChildren = mountedNode.children,
                    node = patch.nextVNode,
                )
                mountedNode.vnode = patch.nextVNode
                moveViewToIndex(
                    container = container,
                    view = mountedNode.view,
                    targetIndex = patch.targetIndex,
                )
                mountedNode
            }
        }
    }

    private fun applyRemoval(
        container: ViewGroup,
        removal: RemovePatch<MountedNode>,
    ) {
        disposeMountedNode(removal.payload)
        container.removeView(removal.payload.view)
    }

    private fun reconcileChildren(
        view: View,
        previousChildren: List<MountedNode>,
        node: VNode,
    ): List<MountedNode> {
        val viewGroup = view as? ViewGroup ?: return emptyList()
        return renderInto(
            container = viewGroup,
            previous = previousChildren,
            nodes = node.children,
        )
    }

    private fun mountNode(context: Context, node: VNode): MountedNode {
        val view = when (node.type) {
            NodeType.Text -> TextView(context)
            NodeType.TextField -> EditText(context)
            NodeType.Checkbox -> CheckBox(context)
            NodeType.Switch -> Switch(context)
            NodeType.RadioButton -> RadioButton(context)
            NodeType.Slider -> SeekBar(context)
            NodeType.Button -> Button(context)
            NodeType.Row -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            NodeType.Column -> DeclarativeLinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            NodeType.Box -> DeclarativeBoxLayout(context)
            NodeType.Spacer -> View(context)
            NodeType.Divider -> View(context)
            NodeType.Image -> View(context)
            NodeType.AndroidView -> readViewFactory(node)?.invoke(context) ?: View(context)
            NodeType.LazyColumn -> RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = LazyColumnAdapter()
            }
            NodeType.TabPager -> DeclarativeTabPagerLayout(context)
        }

        cacheOriginalBackground(view)
        bindView(view, node)
        val children = if (view is ViewGroup) {
            renderInto(
                container = view,
                previous = emptyList(),
                nodes = node.children,
            )
        } else {
            emptyList()
        }
        return MountedNode(
            vnode = node,
            view = view,
            children = children,
        )
    }

    private fun bindView(view: View, node: VNode) {
        applyModifier(view, node)
        when (node.type) {
            NodeType.Text -> {
                (view as TextView).text = node.props.values[PropKeys.TEXT] as? CharSequence
            }

            NodeType.TextField -> {
                bindTextField(view as EditText, node)
            }

            NodeType.Checkbox -> {
                bindCompoundButton(view as CheckBox, node)
            }

            NodeType.Switch -> {
                bindCompoundButton(view as Switch, node)
            }

            NodeType.RadioButton -> {
                bindCompoundButton(view as RadioButton, node)
            }

            NodeType.Slider -> {
                bindSlider(view as SeekBar, node)
            }

            NodeType.Button -> {
                (view as Button).apply {
                    text = node.props.values[PropKeys.TEXT] as? CharSequence
                    setOnClickListener {
                        readOnClick(node)?.invoke()
                    }
                }
            }

            NodeType.Row -> {
                (view as DeclarativeLinearLayout).apply {
                    orientation = LinearLayout.HORIZONTAL
                    itemSpacing = readLinearSpacing(node)
                    mainAxisArrangement = readRowArrangement(node)
                    gravity = readRowVerticalAlignment(node).toGravity()
                }
            }

            NodeType.Column -> {
                (view as DeclarativeLinearLayout).apply {
                    orientation = LinearLayout.VERTICAL
                    itemSpacing = readLinearSpacing(node)
                    mainAxisArrangement = readColumnArrangement(node)
                    gravity = readColumnHorizontalAlignment(node).toGravity()
                }
            }
            NodeType.Box -> {
                (view as DeclarativeBoxLayout).contentGravity = readBoxAlignment(node).toGravity()
            }

            NodeType.Spacer -> Unit

            NodeType.Divider -> {
                view.setBackgroundColor(readDividerColor(node))
            }

            NodeType.Image -> Unit

            NodeType.AndroidView -> {
                readViewUpdate(node)?.invoke(view)
            }

            NodeType.LazyColumn -> {
                (view as RecyclerView).let { recyclerView ->
                    val adapter = recyclerView.adapter as? LazyColumnAdapter ?: LazyColumnAdapter().also {
                        recyclerView.adapter = it
                    }
                    adapter.submitItems(readLazyItems(node))
                }
            }

            NodeType.TabPager -> {
                (view as DeclarativeTabPagerLayout).bind(
                    pages = readTabPages(node),
                    selectedTabIndex = readSelectedTabIndex(node),
                    onTabSelected = readOnTabSelected(node),
                )
            }
        }
    }

    private fun applyModifier(view: View, node: VNode) {
        val alpha = node.modifier.elements
            .lastOrNull { it is AlphaModifierElement } as? AlphaModifierElement
        val backgroundColor = node.modifier.elements
            .lastOrNull { it is BackgroundColorModifierElement } as? BackgroundColorModifierElement
        val clickable = node.modifier.elements
            .lastOrNull { it is ClickableModifierElement } as? ClickableModifierElement
        val offset = node.modifier.elements
            .lastOrNull { it is OffsetModifierElement } as? OffsetModifierElement
        val padding = node.modifier.elements.lastOrNull { it is PaddingModifierElement } as? PaddingModifierElement
        val textColor = node.modifier.elements
            .lastOrNull { it is TextColorModifierElement } as? TextColorModifierElement
        val textSize = node.modifier.elements
            .lastOrNull { it is TextSizeModifierElement } as? TextSizeModifierElement
        val visibility = node.modifier.elements
            .lastOrNull { it is VisibilityModifierElement } as? VisibilityModifierElement
        val zIndex = node.modifier.elements
            .lastOrNull { it is ZIndexModifierElement } as? ZIndexModifierElement
        view.alpha = alpha?.alpha ?: 1f
        if (backgroundColor == null) {
            restoreOriginalBackground(view)
        } else {
            view.setBackgroundColor(backgroundColor.color)
        }
        view.visibility = when (visibility?.visibility ?: Visibility.Visible) {
            Visibility.Visible -> View.VISIBLE
            Visibility.Invisible -> View.INVISIBLE
            Visibility.Gone -> View.GONE
        }
        view.translationX = offset?.x ?: 0f
        view.translationY = offset?.y ?: 0f
        view.z = zIndex?.zIndex ?: 0f
        view.isClickable = clickable != null
        view.setOnClickListener(
            if (clickable == null) {
                null
            } else {
                View.OnClickListener { clickable.onClick() }
            },
        )
        if (padding == null) {
            view.setPadding(0, 0, 0, 0)
        } else {
            view.setPadding(
                padding.left,
                padding.top,
                padding.right,
                padding.bottom,
            )
        }
        if (view is TextView) {
            if (textColor != null) {
                view.setTextColor(textColor.color)
            }
            if (textSize != null) {
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.sizeSp.toFloat())
            }
        }
    }

    private fun cacheOriginalBackground(view: View) {
        if (view.getTag(R.id.ui_framework_original_background) != null) {
            return
        }
        view.setTag(
            R.id.ui_framework_original_background,
            cloneDrawable(view.background),
        )
    }

    private fun restoreOriginalBackground(view: View) {
        view.background = cloneDrawable(
            view.getTag(R.id.ui_framework_original_background) as? Drawable,
        )
    }

    private fun cloneDrawable(drawable: Drawable?): Drawable? {
        return drawable?.constantState?.newDrawable()?.mutate() ?: drawable?.mutate()
    }

    private fun moveViewToIndex(
        container: ViewGroup,
        view: View,
        targetIndex: Int,
    ) {
        val currentIndex = container.indexOfChild(view)
        if (currentIndex == -1 || currentIndex == targetIndex) {
            return
        }
        container.removeViewAt(currentIndex)
        container.addView(
            view,
            targetIndex.coerceAtMost(container.childCount),
        )
    }

    private fun bindTextField(view: EditText, node: VNode) {
        val value = readFieldValue(node)
        if (view.text?.toString() != value) {
            view.setText(value)
            view.setSelection(value.length)
        }
        view.hint = readFieldHint(node)
        view.isSingleLine = readFieldSingleLine(node)
        view.inputType = resolveInputType(
            type = readFieldType(node),
            singleLine = readFieldSingleLine(node),
        )
        view.setHintTextColor(readFieldHintColor(node))
        bindTextWatcher(view, node)
    }

    private fun bindTextWatcher(view: EditText, node: VNode) {
        val previousWatcher = view.getTag(R.id.ui_framework_text_watcher) as? TextWatcher
        if (previousWatcher != null) {
            view.removeTextChangedListener(previousWatcher)
        }
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int,
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) = Unit

            override fun afterTextChanged(s: Editable?) {
                val nextValue = s?.toString().orEmpty()
                if (nextValue != readFieldValue(node)) {
                    readOnValueChange(node)?.invoke(nextValue)
                }
            }
        }
        view.addTextChangedListener(watcher)
        view.setTag(R.id.ui_framework_text_watcher, watcher)
    }

    private fun bindCompoundButton(view: CompoundButton, node: VNode) {
        view.setOnCheckedChangeListener(null)
        view.text = node.props.values[PropKeys.TEXT] as? CharSequence
        view.isEnabled = readEnabled(node)
        view.isChecked = readChecked(node)
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != readChecked(node)) {
                readOnCheckedChange(node)?.invoke(isChecked)
            }
        }
    }

    private fun bindSlider(view: SeekBar, node: VNode) {
        val min = readMinValue(node)
        val max = readMaxValue(node)
        val value = readSliderValue(node).coerceIn(min, max)
        val listener = view.getTag(R.id.ui_framework_seek_listener) as? SeekBar.OnSeekBarChangeListener
        if (listener != null) {
            view.setOnSeekBarChangeListener(null)
        }
        view.max = (max - min).coerceAtLeast(0)
        view.progress = value - min
        view.isEnabled = readEnabled(node)
        val nextListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val resolvedValue = min + progress
                if (fromUser && resolvedValue != readSliderValue(node)) {
                    readOnSliderValueChange(node)?.invoke(resolvedValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        view.setOnSeekBarChangeListener(nextListener)
        view.setTag(R.id.ui_framework_seek_listener, nextListener)
    }

    private fun resolveInputType(type: TextFieldType, singleLine: Boolean): Int {
        val baseType = when (type) {
            TextFieldType.Text -> InputType.TYPE_CLASS_TEXT
            TextFieldType.Password -> {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            TextFieldType.Email -> {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            TextFieldType.Number -> InputType.TYPE_CLASS_NUMBER
        }
        return if (singleLine) {
            baseType
        } else {
            baseType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
    }

    private fun createLayoutParams(parent: ViewGroup, node: VNode): ViewGroup.LayoutParams {
        val boxAlign = node.modifier.elements.lastOrNull { it is BoxAlignModifierElement } as? BoxAlignModifierElement
        val margin = node.modifier.elements.lastOrNull { it is MarginModifierElement } as? MarginModifierElement
        val size = node.modifier.elements.lastOrNull { it is SizeModifierElement } as? SizeModifierElement
        val widthModifier = node.modifier.elements.lastOrNull { it is WidthModifierElement } as? WidthModifierElement
        val heightModifier = node.modifier.elements.lastOrNull { it is HeightModifierElement } as? HeightModifierElement
        val weight = node.modifier.elements.lastOrNull { it is WeightModifierElement } as? WeightModifierElement
        val horizontalAlign = node.modifier.elements
            .lastOrNull { it is HorizontalAlignModifierElement } as? HorizontalAlignModifierElement
        val verticalAlign = node.modifier.elements
            .lastOrNull { it is VerticalAlignModifierElement } as? VerticalAlignModifierElement
        val defaultWidth = when (node.type) {
            NodeType.Text,
            NodeType.TextField,
            NodeType.Checkbox,
            NodeType.Switch,
            NodeType.RadioButton,
            NodeType.Button,
            -> ViewGroup.LayoutParams.WRAP_CONTENT

            NodeType.Spacer -> 0
            NodeType.Divider -> defaultDividerWidth(parent, node)
            else -> ViewGroup.LayoutParams.MATCH_PARENT
        }
        val defaultHeight = when (node.type) {
            NodeType.Spacer -> 0
            NodeType.Divider -> defaultDividerHeight(parent, node)
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
        val width = widthModifier?.width ?: size?.width ?: defaultWidth
        val height = heightModifier?.height ?: size?.height ?: defaultHeight
        return when (parent) {
            is DeclarativeLinearLayout -> {
                val resolvedWidth = if (
                    weight != null &&
                    parent.orientation == LinearLayout.HORIZONTAL &&
                    widthModifier == null &&
                    size?.width == null
                ) {
                    0
                } else {
                    width
                }
                val resolvedHeight = if (
                    weight != null &&
                    parent.orientation == LinearLayout.VERTICAL &&
                    heightModifier == null &&
                    size?.height == null
                ) {
                    0
                } else {
                    height
                }
                android.widget.LinearLayout.LayoutParams(resolvedWidth, resolvedHeight).applyLayoutParams(
                    margin = margin,
                ) {
                    this.weight = weight?.weight ?: 0f
                    gravity = when (parent.orientation) {
                        LinearLayout.HORIZONTAL -> verticalAlign?.alignment?.toGravity() ?: -1
                        else -> horizontalAlign?.alignment?.toGravity() ?: -1
                    }
                }
            }
            is DeclarativeBoxLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(
                margin = margin,
            ) {
                gravity = boxAlign?.alignment?.toGravity() ?: DeclarativeBoxLayout.UNSET_GRAVITY
            }
            is FrameLayout -> FrameLayout.LayoutParams(width, height).applyLayoutParams(margin = margin)
            else -> ViewGroup.MarginLayoutParams(width, height).applyMargin(margin)
        }
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyLayoutParams(
        margin: MarginModifierElement?,
        block: T.() -> Unit = {},
    ): T {
        applyMargin(margin)
        block()
        return this
    }

    private fun <T : ViewGroup.MarginLayoutParams> T.applyMargin(
        margin: MarginModifierElement?,
    ): T {
        if (margin == null) {
            setMargins(0, 0, 0, 0)
            return this
        }
        setMargins(
            margin.left,
            margin.top,
            margin.right,
            margin.bottom,
        )
        return this
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnClick(node: VNode): (() -> Unit)? {
        return node.props.values[PropKeys.ON_CLICK] as? (() -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnValueChange(node: VNode): ((String) -> Unit)? {
        return node.props.values[PropKeys.ON_VALUE_CHANGE] as? ((String) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnCheckedChange(node: VNode): ((Boolean) -> Unit)? {
        return node.props.values[PropKeys.ON_CHECKED_CHANGE] as? ((Boolean) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnSliderValueChange(node: VNode): ((Int) -> Unit)? {
        return node.props.values[PropKeys.ON_SLIDER_VALUE_CHANGE] as? ((Int) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readViewFactory(node: VNode): ((Context) -> View)? {
        return node.props.values[PropKeys.VIEW_FACTORY] as? ((Context) -> View)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readViewUpdate(node: VNode): ((View) -> Unit)? {
        return node.props.values[PropKeys.VIEW_UPDATE] as? ((View) -> Unit)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readLazyItems(node: VNode): List<LazyListItem> {
        return node.props.values[PropKeys.LAZY_ITEMS] as? List<LazyListItem> ?: emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    private fun readTabPages(node: VNode): List<TabPage> {
        return node.props.values[PropKeys.TAB_PAGES] as? List<TabPage> ?: emptyList()
    }

    private fun readSelectedTabIndex(node: VNode): Int {
        return node.props.values[PropKeys.SELECTED_TAB_INDEX] as? Int ?: 0
    }

    @Suppress("UNCHECKED_CAST")
    private fun readOnTabSelected(node: VNode): ((Int) -> Unit)? {
        return node.props.values[PropKeys.ON_TAB_SELECTED] as? ((Int) -> Unit)
    }

    private fun readLinearSpacing(node: VNode): Int {
        return node.props.values[PropKeys.LINEAR_SPACING] as? Int ?: 0
    }

    private fun readBoxAlignment(node: VNode): BoxAlignment {
        return node.props.values[PropKeys.BOX_ALIGNMENT] as? BoxAlignment
            ?: BoxAlignment.TopStart
    }

    private fun readRowVerticalAlignment(node: VNode): VerticalAlignment {
        return node.props.values[PropKeys.ROW_VERTICAL_ALIGNMENT] as? VerticalAlignment
            ?: VerticalAlignment.Top
    }

    private fun readRowArrangement(node: VNode): MainAxisArrangement {
        return node.props.values[PropKeys.ROW_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
            ?: MainAxisArrangement.Start
    }

    private fun readColumnHorizontalAlignment(node: VNode): HorizontalAlignment {
        return node.props.values[PropKeys.COLUMN_HORIZONTAL_ALIGNMENT] as? HorizontalAlignment
            ?: HorizontalAlignment.Start
    }

    private fun readColumnArrangement(node: VNode): MainAxisArrangement {
        return node.props.values[PropKeys.COLUMN_MAIN_AXIS_ARRANGEMENT] as? MainAxisArrangement
            ?: MainAxisArrangement.Start
    }

    private fun defaultDividerWidth(parent: ViewGroup, node: VNode): Int {
        val thickness = readDividerThickness(node)
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            thickness
        } else {
            ViewGroup.LayoutParams.MATCH_PARENT
        }
    }

    private fun defaultDividerHeight(parent: ViewGroup, node: VNode): Int {
        val thickness = readDividerThickness(node)
        return if ((parent as? LinearLayout)?.orientation == LinearLayout.HORIZONTAL) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            thickness
        }
    }

    private fun readDividerColor(node: VNode): Int {
        return node.props.values[PropKeys.DIVIDER_COLOR] as? Int ?: 0xFF000000.toInt()
    }

    private fun readDividerThickness(node: VNode): Int {
        return node.props.values[PropKeys.DIVIDER_THICKNESS] as? Int ?: 1
    }

    private fun readChecked(node: VNode): Boolean {
        return node.props.values[PropKeys.CHECKED] as? Boolean ?: false
    }

    private fun readEnabled(node: VNode): Boolean {
        return node.props.values[PropKeys.ENABLED] as? Boolean ?: true
    }

    private fun readFieldValue(node: VNode): String {
        return node.props.values[PropKeys.VALUE] as? String ?: ""
    }

    private fun readFieldHint(node: VNode): String {
        return node.props.values[PropKeys.HINT] as? String ?: ""
    }

    private fun readFieldSingleLine(node: VNode): Boolean {
        return node.props.values[PropKeys.SINGLE_LINE] as? Boolean ?: true
    }

    private fun readFieldType(node: VNode): TextFieldType {
        return node.props.values[PropKeys.TEXT_FIELD_TYPE] as? TextFieldType ?: TextFieldType.Text
    }

    private fun readFieldHintColor(node: VNode): Int {
        return node.props.values[PropKeys.HINT_TEXT_COLOR] as? Int ?: 0xFF888888.toInt()
    }

    private fun readSliderValue(node: VNode): Int {
        return node.props.values[PropKeys.SLIDER_VALUE] as? Int ?: 0
    }

    private fun readMinValue(node: VNode): Int {
        return node.props.values[PropKeys.MIN_VALUE] as? Int ?: 0
    }

    private fun readMaxValue(node: VNode): Int {
        return node.props.values[PropKeys.MAX_VALUE] as? Int ?: 100
    }

    private fun disposeMountedNode(
        mountedNode: MountedNode,
    ) {
        mountedNode.children.forEach(::disposeMountedNode)
        (mountedNode.view as? DeclarativeTabPagerLayout)?.dispose()
        (mountedNode.view as? RecyclerView)
            ?.adapter
            ?.let { adapter ->
                (adapter as? LazyColumnAdapter)?.disposeAll()
            }
        mountedNode.children = emptyList()
    }

    private fun VerticalAlignment.toGravity(): Int {
        return when (this) {
            VerticalAlignment.Top -> Gravity.TOP
            VerticalAlignment.Center -> Gravity.CENTER_VERTICAL
            VerticalAlignment.Bottom -> Gravity.BOTTOM
        }
    }

    private fun HorizontalAlignment.toGravity(): Int {
        return when (this) {
            HorizontalAlignment.Start -> Gravity.START
            HorizontalAlignment.Center -> Gravity.CENTER_HORIZONTAL
            HorizontalAlignment.End -> Gravity.END
        }
    }

    private fun BoxAlignment.toGravity(): Int {
        return when (this) {
            BoxAlignment.TopStart -> Gravity.TOP or Gravity.START
            BoxAlignment.TopCenter -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            BoxAlignment.TopEnd -> Gravity.TOP or Gravity.END
            BoxAlignment.CenterStart -> Gravity.CENTER_VERTICAL or Gravity.START
            BoxAlignment.Center -> Gravity.CENTER
            BoxAlignment.CenterEnd -> Gravity.CENTER_VERTICAL or Gravity.END
            BoxAlignment.BottomStart -> Gravity.BOTTOM or Gravity.START
            BoxAlignment.BottomCenter -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            BoxAlignment.BottomEnd -> Gravity.BOTTOM or Gravity.END
        }
    }
}
