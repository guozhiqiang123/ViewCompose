package com.gzq.uiframework.renderer.view.container

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.gzq.uiframework.renderer.view.tree.LayoutPassTracker

internal class DeclarativeTextFieldLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {
    val inputView: EditText = EditText(context).apply {
        background = null
        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    val fieldContainer: FrameLayout = FrameLayout(context).apply {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
        addView(inputView)
    }

    private val labelView: TextView = TextView(context).apply {
        visibility = View.GONE
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    private val supportingView: TextView = TextView(context).apply {
        visibility = View.GONE
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )
    }

    init {
        orientation = VERTICAL
        addView(labelView)
        addView(fieldContainer)
        addView(supportingView)
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int,
    ) {
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

    fun setLabel(
        text: String,
        color: Int,
        textSizeSp: Int,
    ) {
        if (text.isBlank()) {
            labelView.visibility = View.GONE
            labelView.text = ""
            return
        }
        labelView.visibility = View.VISIBLE
        labelView.text = text
        labelView.setTextColor(color)
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
    }

    fun setSupportingText(
        text: String,
        color: Int,
        textSizeSp: Int,
    ) {
        if (text.isBlank()) {
            supportingView.visibility = View.GONE
            supportingView.text = ""
            return
        }
        supportingView.visibility = View.VISIBLE
        supportingView.text = text
        supportingView.setTextColor(color)
        supportingView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSp.toFloat())
    }
}
