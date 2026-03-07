package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.view.dpToPx

internal class DeclarativeSegmentedControlLayout(
    context: Context,
) : LinearLayout(context) {
    private var items: List<SegmentedControlItem> = emptyList()
    private var selectedIndex: Int = -1
    private var onSelectionChange: ((Int) -> Unit)? = null
    private val indicatorInset = context.dpToPx(2).toFloat()
    private val containerBackground = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        clipToPadding = false
        background = containerBackground
    }

    fun bind(
        items: List<SegmentedControlItem>,
        selectedIndex: Int,
        onSelectionChange: ((Int) -> Unit)?,
        enabled: Boolean,
        backgroundColor: Int,
        indicatorColor: Int,
        cornerRadius: Int,
        textColor: Int,
        selectedTextColor: Int,
        rippleColor: Int,
        textSizeSp: Int,
        paddingHorizontal: Int,
        paddingVertical: Int,
    ) {
        this.onSelectionChange = onSelectionChange
        containerBackground.setColor(backgroundColor)
        containerBackground.cornerRadius = cornerRadius.toFloat()
        if (this.items.map { it.label } != items.map { it.label }) {
            rebuild(items)
        }
        this.items = items
        this.selectedIndex = selectedIndex
        updateChildren(
            enabled = enabled,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            rippleColor = rippleColor,
            textSizeSp = textSizeSp,
            paddingHorizontal = paddingHorizontal,
            paddingVertical = paddingVertical,
        )
    }

    private fun rebuild(items: List<SegmentedControlItem>) {
        removeAllViews()
        items.forEachIndexed { index, _ ->
            addView(
                TextView(context).apply {
                    gravity = Gravity.CENTER
                    ellipsize = TextUtils.TruncateAt.END
                    maxLines = 1
                    layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
                    setOnClickListener {
                        if (isEnabled) {
                            onSelectionChange?.invoke(index)
                        }
                    }
                },
            )
        }
    }

    private fun updateChildren(
        enabled: Boolean,
        indicatorColor: Int,
        cornerRadius: Int,
        textColor: Int,
        selectedTextColor: Int,
        rippleColor: Int,
        textSizeSp: Int,
        paddingHorizontal: Int,
        paddingVertical: Int,
    ) {
        val insetPx = indicatorInset.toInt()
        for (index in 0 until childCount) {
            val child = getChildAt(index) as? TextView ?: continue
            val item = items.getOrNull(index) ?: continue
            val isSelected = index == selectedIndex
            child.text = item.label
            child.isEnabled = enabled
            child.setTextColor(if (isSelected) selectedTextColor else textColor)
            child.textSize = textSizeSp.toFloat()
            child.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            val childParams = child.layoutParams as LayoutParams
            if (childParams.leftMargin != insetPx ||
                childParams.topMargin != insetPx ||
                childParams.rightMargin != insetPx ||
                childParams.bottomMargin != insetPx
            ) {
                childParams.leftMargin = insetPx
                childParams.topMargin = insetPx
                childParams.rightMargin = insetPx
                childParams.bottomMargin = insetPx
                child.layoutParams = childParams
            }
            child.background = createSegmentBackground(
                enabled = enabled,
                selected = isSelected,
                indicatorColor = indicatorColor,
                rippleColor = rippleColor,
                cornerRadius = (cornerRadius - indicatorInset).coerceAtLeast(0f),
            )
            child.isSelected = isSelected
        }
    }

    private fun createSegmentBackground(
        enabled: Boolean,
        selected: Boolean,
        indicatorColor: Int,
        rippleColor: Int,
        cornerRadius: Float,
    ) = if (enabled) {
        RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(if (selected) indicatorColor else Color.TRANSPARENT)
                this.cornerRadius = cornerRadius
            },
            GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.WHITE)
                this.cornerRadius = cornerRadius
            },
        )
    } else {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(if (selected) indicatorColor else Color.TRANSPARENT)
            this.cornerRadius = cornerRadius
        }
    }
}
