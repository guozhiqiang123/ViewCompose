package com.gzq.uiframework.renderer.view.container

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
import com.gzq.uiframework.renderer.node.NavigationBarItem

internal class DeclarativeNavigationBarLayout(
    context: Context,
) : LinearLayout(context) {
    private var items: List<NavigationBarItem> = emptyList()
    private var selectedIndex: Int = -1
    private var onItemSelected: ((Int) -> Unit)? = null
    private val density = context.resources.displayMetrics.density

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
        this.onItemSelected = onItemSelected
        setBackgroundColor(containerColor)
        val labelsChanged = this.items.map { it.label } != items.map { it.label }
        val iconsChanged = this.items.map { it.icon.resId } != items.map { it.icon.resId }
        if (labelsChanged || iconsChanged || childCount != items.size) {
            rebuild(items, rippleColor)
        }
        this.items = items
        this.selectedIndex = selectedIndex
        updateChildren(
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

    private fun rebuild(items: List<NavigationBarItem>, rippleColor: Int) {
        removeAllViews()
        items.forEachIndexed { index, item ->
            addView(createItemView(index, item, rippleColor))
        }
    }

    private fun createItemView(index: Int, item: NavigationBarItem, rippleColor: Int): View {
        val itemLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            setPadding(0, dpToPx(12), 0, dpToPx(16))
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
                dpToPx(INDICATOR_WIDTH),
                dpToPx(INDICATOR_HEIGHT),
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        // Pill indicator (behind icon)
        val indicator = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(INDICATOR_WIDTH),
                dpToPx(INDICATOR_HEIGHT),
            ).apply {
                gravity = Gravity.CENTER
            }
            tag = TAG_INDICATOR
        }
        iconContainer.addView(indicator)

        // Icon
        val iconView = ImageView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(ICON_SIZE_DEFAULT),
                dpToPx(ICON_SIZE_DEFAULT),
            ).apply {
                gravity = Gravity.CENTER
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(item.icon.resId)
            tag = TAG_ICON
        }
        iconContainer.addView(iconView)

        // Badge (optional, positioned top-right of icon container)
        val badgeView = TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                marginStart = dpToPx(INDICATOR_WIDTH / 2)
            }
            gravity = Gravity.CENTER
            setTypeface(typeface, Typeface.BOLD)
            includeFontPadding = false
            tag = TAG_BADGE
        }
        iconContainer.addView(badgeView)

        itemLayout.addView(iconContainer)

        // Spacer
        val spacer = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(4),
            )
        }
        itemLayout.addView(spacer)

        // Label
        val label = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
            this.gravity = Gravity.CENTER
            text = item.label
            maxLines = 1
            tag = TAG_LABEL
        }
        itemLayout.addView(label)

        return itemLayout
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
        for (index in 0 until childCount) {
            val itemLayout = getChildAt(index) as? LinearLayout ?: continue
            val item = items.getOrNull(index) ?: continue
            val isSelected = index == selectedIndex

            val iconContainer = itemLayout.getChildAt(0) as? FrameLayout ?: continue
            val indicator = iconContainer.findViewWithTag<View>(TAG_INDICATOR)
            val iconView = iconContainer.findViewWithTag<ImageView>(TAG_ICON)
            val badgeView = iconContainer.findViewWithTag<TextView>(TAG_BADGE)
            val label = itemLayout.findViewWithTag<TextView>(TAG_LABEL)

            // Update indicator
            indicator?.apply {
                visibility = if (isSelected) View.VISIBLE else View.INVISIBLE
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(indicatorColor)
                    cornerRadius = dpToPx(INDICATOR_CORNER_RADIUS).toFloat()
                }
            }

            // Update icon
            iconView?.apply {
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
                layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                    width = size
                    height = size
                }
            }

            // Update badge
            badgeView?.apply {
                when {
                    item.badgeCount == null -> {
                        visibility = View.GONE
                    }
                    item.badgeCount == 0 -> {
                        // Dot badge
                        visibility = View.VISIBLE
                        text = ""
                        val dotSize = dpToPx(DOT_BADGE_SIZE)
                        layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                            width = dotSize
                            height = dotSize
                        }
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(badgeColor)
                        }
                    }
                    else -> {
                        // Number badge
                        visibility = View.VISIBLE
                        text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString()
                        setTextColor(badgeTextColor)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, BADGE_TEXT_SIZE_SP)
                        val badgeHeight = dpToPx(BADGE_HEIGHT)
                        val hPad = dpToPx(BADGE_HORIZONTAL_PADDING)
                        setPadding(hPad, 0, hPad, 0)
                        layoutParams = (layoutParams as FrameLayout.LayoutParams).apply {
                            width = ViewGroup.LayoutParams.WRAP_CONTENT
                            height = badgeHeight
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

            // Update label
            label?.apply {
                text = item.label
                setTextColor(if (isSelected) selectedLabelColor else unselectedLabelColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, labelSizeSp.toFloat())
            }
        }
    }

    private fun dpToPx(dp: Int): Int = (dp * density + 0.5f).toInt()

    companion object {
        private const val TAG_INDICATOR = "nav_indicator"
        private const val TAG_ICON = "nav_icon"
        private const val TAG_BADGE = "nav_badge"
        private const val TAG_LABEL = "nav_label"

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
