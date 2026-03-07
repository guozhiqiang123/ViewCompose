package com.viewcompose.renderer.view.container

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.viewcompose.renderer.node.NavigationBarItem
import com.viewcompose.renderer.view.dpToPx

internal class DeclarativeNavigationBarLayout(
    context: Context,
) : LinearLayout(context) {
    private data class ItemViewRefs(
        val root: LinearLayout,
        val indicator: View,
        val indicatorDrawable: GradientDrawable,
        val iconView: ImageView,
        val badgeView: TextView,
        val labelView: TextView,
    )

    private var items: List<NavigationBarItem> = emptyList()
    private var selectedIndex: Int = -1
    private var onItemSelected: ((Int) -> Unit)? = null
    private var styleInitialized: Boolean = false
    private var containerColorState: Int = Color.TRANSPARENT
    private var selectedIconColorState: Int = Color.TRANSPARENT
    private var unselectedIconColorState: Int = Color.TRANSPARENT
    private var selectedLabelColorState: Int = Color.TRANSPARENT
    private var unselectedLabelColorState: Int = Color.TRANSPARENT
    private var indicatorColorState: Int = Color.TRANSPARENT
    private var rippleColorState: Int = Color.TRANSPARENT
    private var iconSizeState: Int = 0
    private var labelSizeSpState: Int = 0
    private var badgeColorState: Int = Color.TRANSPARENT
    private var badgeTextColorState: Int = Color.TRANSPARENT
    private val itemRefs = mutableListOf<ItemViewRefs>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.BOTTOM
    }

    fun bind(
        items: List<NavigationBarItem>,
        selectedIndex: Int,
        onItemSelected: ((Int) -> Unit)?,
        containerColor: Int,
        selectedIconColor: Int,
        unselectedIconColor: Int,
        selectedLabelColor: Int,
        unselectedLabelColor: Int,
        indicatorColor: Int,
        rippleColor: Int,
        iconSize: Int,
        labelSizeSp: Int,
        badgeColor: Int,
        badgeTextColor: Int,
    ) {
        val previousItems = this.items
        val previousSelectedIndex = this.selectedIndex
        this.onItemSelected = onItemSelected

        val resolvedSelectedIndex = if (items.isEmpty()) {
            -1
        } else {
            selectedIndex.coerceIn(0, items.lastIndex)
        }
        val labelsChanged = previousItems.map { it.label } != items.map { it.label }
        val iconsChanged = previousItems.map { it.icon.resId } != items.map { it.icon.resId }
        val rippleChanged = !styleInitialized || rippleColorState != rippleColor
        val structureChanged = labelsChanged || iconsChanged || childCount != items.size || rippleChanged
        if (structureChanged) {
            rebuild(items, rippleColor)
        }
        val styleChanged = !styleInitialized ||
            selectedIconColorState != selectedIconColor ||
            unselectedIconColorState != unselectedIconColor ||
            selectedLabelColorState != selectedLabelColor ||
            unselectedLabelColorState != unselectedLabelColor ||
            indicatorColorState != indicatorColor ||
            iconSizeState != iconSize ||
            labelSizeSpState != labelSizeSp ||
            badgeColorState != badgeColor ||
            badgeTextColorState != badgeTextColor
        val selectionChanged = previousSelectedIndex != resolvedSelectedIndex
        val contentChangedIndices = if (structureChanged) {
            emptySet()
        } else {
            calculateChangedItemIndices(
                previousItems = previousItems,
                nextItems = items,
            )
        }
        if (!styleInitialized || containerColorState != containerColor) {
            setBackgroundColor(containerColor)
        }

        this.items = items
        this.selectedIndex = resolvedSelectedIndex

        containerColorState = containerColor
        selectedIconColorState = selectedIconColor
        unselectedIconColorState = unselectedIconColor
        selectedLabelColorState = selectedLabelColor
        unselectedLabelColorState = unselectedLabelColor
        indicatorColorState = indicatorColor
        rippleColorState = rippleColor
        iconSizeState = iconSize
        labelSizeSpState = labelSizeSp
        badgeColorState = badgeColor
        badgeTextColorState = badgeTextColor
        styleInitialized = true

        when {
            structureChanged || styleChanged -> updateChildren(
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                selectedLabelColor = selectedLabelColor,
                unselectedLabelColor = unselectedLabelColor,
                indicatorColor = indicatorColor,
                iconSize = iconSize,
                labelSizeSp = labelSizeSp,
                badgeColor = badgeColor,
                badgeTextColor = badgeTextColor,
            )

            selectionChanged || contentChangedIndices.isNotEmpty() -> {
                val indices = linkedSetOf<Int>()
                if (selectionChanged) {
                    indices += previousSelectedIndex
                    indices += resolvedSelectedIndex
                }
                indices += contentChangedIndices
                updateChildrenAt(
                    indices = indices,
                    selectedIconColor = selectedIconColor,
                    unselectedIconColor = unselectedIconColor,
                    selectedLabelColor = selectedLabelColor,
                    unselectedLabelColor = unselectedLabelColor,
                    indicatorColor = indicatorColor,
                    iconSize = iconSize,
                    labelSizeSp = labelSizeSp,
                    badgeColor = badgeColor,
                    badgeTextColor = badgeTextColor,
                )
            }
        }
    }

    private fun rebuild(items: List<NavigationBarItem>, rippleColor: Int) {
        itemRefs.clear()
        removeAllViews()
        items.forEachIndexed { index, item ->
            val refs = createItemView(index, item, rippleColor)
            itemRefs += refs
            addView(refs.root)
        }
    }

    private fun createItemView(
        index: Int,
        item: NavigationBarItem,
        rippleColor: Int,
    ): ItemViewRefs {
        val itemLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            setPadding(0, context.dpToPx(12), 0, context.dpToPx(16))
            isClickable = true
            isFocusable = true
            background = RippleDrawable(
                ColorStateList.valueOf(rippleColor),
                null,
                GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(Color.WHITE)
                },
            )
            setOnClickListener {
                onItemSelected?.invoke(index)
            }
        }

        // Icon container (FrameLayout for overlaying indicator + icon + badge)
        val iconContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                context.dpToPx(INDICATOR_WIDTH),
                context.dpToPx(INDICATOR_HEIGHT),
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        // Pill indicator (behind icon)
        val indicatorDrawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
        }
        val indicator = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                context.dpToPx(INDICATOR_WIDTH),
                context.dpToPx(INDICATOR_HEIGHT),
            ).apply {
                gravity = Gravity.CENTER
            }
            background = indicatorDrawable
        }
        iconContainer.addView(indicator)

        // Icon
        val iconView = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                context.dpToPx(ICON_SIZE_DEFAULT),
                context.dpToPx(ICON_SIZE_DEFAULT),
            ).apply {
                gravity = Gravity.CENTER
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(item.icon.resId)
        }
        iconContainer.addView(iconView)

        // Badge (optional, positioned top-right of icon container)
        val badgeView = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                marginStart = context.dpToPx(INDICATOR_WIDTH / 2)
            }
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            includeFontPadding = false
        }
        iconContainer.addView(badgeView)

        itemLayout.addView(iconContainer)

        // Label
        val label = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = context.dpToPx(4)
            }
            this.gravity = Gravity.CENTER
            text = item.label
            maxLines = 1
        }
        itemLayout.addView(label)

        return ItemViewRefs(
            root = itemLayout,
            indicator = indicator,
            indicatorDrawable = indicatorDrawable,
            iconView = iconView,
            badgeView = badgeView,
            labelView = label,
        )
    }

    private fun updateChildren(
        selectedIconColor: Int,
        unselectedIconColor: Int,
        selectedLabelColor: Int,
        unselectedLabelColor: Int,
        indicatorColor: Int,
        iconSize: Int,
        labelSizeSp: Int,
        badgeColor: Int,
        badgeTextColor: Int,
    ) {
        for (index in 0 until itemRefs.size) {
            updateChildAt(
                index = index,
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                selectedLabelColor = selectedLabelColor,
                unselectedLabelColor = unselectedLabelColor,
                indicatorColor = indicatorColor,
                iconSize = iconSize,
                labelSizeSp = labelSizeSp,
                badgeColor = badgeColor,
                badgeTextColor = badgeTextColor,
            )
        }
    }

    private fun updateChildrenAt(
        indices: Set<Int>,
        selectedIconColor: Int,
        unselectedIconColor: Int,
        selectedLabelColor: Int,
        unselectedLabelColor: Int,
        indicatorColor: Int,
        iconSize: Int,
        labelSizeSp: Int,
        badgeColor: Int,
        badgeTextColor: Int,
    ) {
        indices.forEach { index ->
            updateChildAt(
                index = index,
                selectedIconColor = selectedIconColor,
                unselectedIconColor = unselectedIconColor,
                selectedLabelColor = selectedLabelColor,
                unselectedLabelColor = unselectedLabelColor,
                indicatorColor = indicatorColor,
                iconSize = iconSize,
                labelSizeSp = labelSizeSp,
                badgeColor = badgeColor,
                badgeTextColor = badgeTextColor,
            )
        }
    }

    private fun updateChildAt(
        index: Int,
        selectedIconColor: Int,
        unselectedIconColor: Int,
        selectedLabelColor: Int,
        unselectedLabelColor: Int,
        indicatorColor: Int,
        iconSize: Int,
        labelSizeSp: Int,
        badgeColor: Int,
        badgeTextColor: Int,
    ) {
        if (index !in itemRefs.indices) {
            return
        }
        val item = items.getOrNull(index) ?: return
        val isSelected = index == selectedIndex
        val refs = itemRefs[index]

        refs.indicator.apply {
            visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
        }
        refs.indicatorDrawable.setColor(indicatorColor)
        refs.indicatorDrawable.cornerRadius = context.dpToPx(INDICATOR_CORNER_RADIUS).toFloat()

        refs.iconView.apply {
            val iconRes = if (isSelected && item.selectedIcon != null) {
                item.selectedIcon.resId
            } else {
                item.icon.resId
            }
            setImageResource(iconRes)
            imageTintList = ColorStateList.valueOf(
                if (isSelected) selectedIconColor else unselectedIconColor,
            )
            val size = iconSize.coerceAtLeast(1)
            val currentParams = layoutParams as FrameLayout.LayoutParams
            if (currentParams.width != size || currentParams.height != size) {
                layoutParams = currentParams.apply {
                    width = size
                    height = size
                }
            }
        }

        refs.badgeView.apply {
            when {
                item.badgeCount == null -> {
                    visibility = View.GONE
                }
                item.badgeCount == 0 -> {
                    visibility = View.VISIBLE
                    text = ""
                    val dotSize = context.dpToPx(DOT_BADGE_SIZE)
                    val currentParams = layoutParams as FrameLayout.LayoutParams
                    if (currentParams.width != dotSize || currentParams.height != dotSize) {
                        layoutParams = currentParams.apply {
                            width = dotSize
                            height = dotSize
                        }
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(badgeColor)
                    }
                }
                else -> {
                    visibility = View.VISIBLE
                    text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString()
                    setTextColor(badgeTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, BADGE_TEXT_SIZE_SP)
                    val badgeHeight = context.dpToPx(BADGE_HEIGHT)
                    val hPad = context.dpToPx(BADGE_HORIZONTAL_PADDING)
                    setPadding(hPad, 0, hPad, 0)
                    val currentParams = layoutParams as FrameLayout.LayoutParams
                    if (currentParams.width != ViewGroup.LayoutParams.WRAP_CONTENT || currentParams.height != badgeHeight) {
                        layoutParams = currentParams.apply {
                            width = ViewGroup.LayoutParams.WRAP_CONTENT
                            height = badgeHeight
                        }
                    }
                    minWidth = badgeHeight
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        setColor(badgeColor)
                        cornerRadius = badgeHeight / 2f
                    }
                }
            }
        }

        refs.labelView.apply {
            text = item.label
            setTextColor(if (isSelected) selectedLabelColor else unselectedLabelColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, labelSizeSp.toFloat())
        }
    }

    private fun calculateChangedItemIndices(
        previousItems: List<NavigationBarItem>,
        nextItems: List<NavigationBarItem>,
    ): Set<Int> {
        if (previousItems.size != nextItems.size) {
            return nextItems.indices.toSet()
        }
        val indices = linkedSetOf<Int>()
        for (index in nextItems.indices) {
            if (previousItems[index] != nextItems[index]) {
                indices += index
            }
        }
        return indices
    }

    companion object {
        private const val INDICATOR_WIDTH = 64
        private const val INDICATOR_HEIGHT = 32
        private const val INDICATOR_CORNER_RADIUS = 16
        private const val ICON_SIZE_DEFAULT = 24
        private const val DOT_BADGE_SIZE = 6
        private const val BADGE_HEIGHT = 16
        private const val BADGE_HORIZONTAL_PADDING = 4
        private const val BADGE_TEXT_SIZE_SP = 10f
    }
}
