package com.gzq.uiframework.renderer.view.tree.patch

import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Switch
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import com.gzq.uiframework.renderer.view.tree.InputViewBinder
import com.gzq.uiframework.renderer.view.tree.SliderNodePatch
import com.gzq.uiframework.renderer.view.tree.TextFieldNodePatch
import com.gzq.uiframework.renderer.view.tree.ToggleNodePatch
import com.gzq.uiframework.renderer.view.tree.ViewModifierApplier

internal object InputNodePatchApplier {
    fun applyTextFieldPatch(
        view: DeclarativeTextFieldLayout,
        patch: TextFieldNodePatch,
    ) {
        val input = view.inputView
        val previous = patch.previous
        val next = patch.next
        if (previous.value != next.value && input.text?.toString() != next.value) {
            input.setText(next.value)
            input.setSelection(next.value.length)
        }
        if (
            previous.label != next.label ||
            previous.labelColor != next.labelColor ||
            previous.labelTextSizeSp != next.labelTextSizeSp
        ) {
            view.setLabel(
                text = next.label,
                color = next.labelColor,
                textSizeSp = next.labelTextSizeSp,
            )
        }
        if (
            previous.supportingText != next.supportingText ||
            previous.supportingTextColor != next.supportingTextColor ||
            previous.supportingTextSizeSp != next.supportingTextSizeSp
        ) {
            view.setSupportingText(
                text = next.supportingText,
                color = next.supportingTextColor,
                textSizeSp = next.supportingTextSizeSp,
            )
        }
        if (previous.placeholder != next.placeholder) {
            input.hint = next.placeholder
        }
        if (previous.enabled != next.enabled) {
            input.isEnabled = next.enabled
        }
        if (previous.singleLine != next.singleLine) {
            input.isSingleLine = next.singleLine
        }
        if (
            previous.singleLine != next.singleLine ||
            previous.minLines != next.minLines
        ) {
            input.minLines = if (next.singleLine) 1 else next.minLines
        }
        if (
            previous.singleLine != next.singleLine ||
            previous.maxLines != next.maxLines
        ) {
            input.maxLines = if (next.singleLine) 1 else next.maxLines
        }
        if (
            previous.keyboardType != next.keyboardType ||
            previous.singleLine != next.singleLine
        ) {
            input.inputType = InputViewBinder.resolveInputType(
                type = next.keyboardType,
                singleLine = next.singleLine,
            )
        }
        if (previous.imeAction != next.imeAction) {
            input.imeOptions = InputViewBinder.toEditorAction(next.imeAction)
        }
        if (previous.hintColor != next.hintColor) {
            input.setHintTextColor(next.hintColor)
        }
        if (previous.cursorColor != next.cursorColor && next.cursorColor != 0) {
            input.highlightColor = next.cursorColor
        }
        if (previous.readOnly != next.readOnly) {
            InputViewBinder.applyReadOnly(input, next.readOnly)
        }
        if (previous.maxLength != next.maxLength) {
            InputViewBinder.applyMaxLength(input, next.maxLength)
        }
        if (
            previous.value != next.value ||
            previous.onValueChange != next.onValueChange
        ) {
            InputViewBinder.bindTextWatcher(
                view = input,
                currentValue = next.value,
                onValueChange = next.onValueChange,
            )
        }
        if (previous.textColor != next.textColor) {
            input.setTextColor(next.textColor)
        }
        if (previous.textSizeSp != next.textSizeSp) {
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, next.textSizeSp.toFloat())
        }
        if (
            previous.backgroundColor != next.backgroundColor ||
            previous.borderWidth != next.borderWidth ||
            previous.borderColor != next.borderColor ||
            previous.cornerRadius != next.cornerRadius ||
            previous.rippleColor != next.rippleColor
        ) {
            ViewModifierApplier.applyStylePatch(
                view = view.fieldContainer,
                backgroundColor = next.backgroundColor,
                borderWidth = next.borderWidth,
                borderColor = next.borderColor,
                cornerRadius = next.cornerRadius,
                rippleColor = next.rippleColor,
                clickable = false,
            )
        }
        if (previous.minHeight != next.minHeight) {
            view.fieldContainer.minimumHeight = next.minHeight
        }
        if (
            previous.paddingHorizontal != next.paddingHorizontal ||
            previous.paddingVertical != next.paddingVertical
        ) {
            view.fieldContainer.setPadding(
                next.paddingHorizontal,
                next.paddingVertical,
                next.paddingHorizontal,
                next.paddingVertical,
            )
        }
    }

    fun applyTogglePatch(
        view: CompoundButton,
        patch: ToggleNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        view.setOnCheckedChangeListener(null)
        if (previous.text != next.text) {
            view.text = next.text
        }
        if (previous.enabled != next.enabled) {
            view.isEnabled = next.enabled
        }
        if (previous.checked != next.checked) {
            view.isChecked = next.checked
        }
        if (previous.controlColor != next.controlColor ||
            previous.checkedColor != next.checkedColor ||
            previous.uncheckedColor != next.uncheckedColor
        ) {
            if (view is Switch) {
                view.buttonTintList = ColorStateList.valueOf(next.controlColor)
                view.thumbTintList = ColorStateList.valueOf(next.thumbColor ?: next.controlColor)
                view.trackTintList = ColorStateList.valueOf(next.trackColor ?: next.controlColor)
            } else if (next.checkedColor != null || next.uncheckedColor != null) {
                val checked = next.checkedColor ?: next.controlColor
                val unchecked = next.uncheckedColor ?: next.controlColor
                view.buttonTintList = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_checked),
                        intArrayOf(-android.R.attr.state_checked),
                    ),
                    intArrayOf(checked, unchecked),
                )
            } else {
                view.buttonTintList = ColorStateList.valueOf(next.controlColor)
            }
        } else if (view is Switch) {
            if (previous.thumbColor != next.thumbColor) {
                view.thumbTintList = ColorStateList.valueOf(next.thumbColor ?: next.controlColor)
            }
            if (previous.trackColor != next.trackColor) {
                view.trackTintList = ColorStateList.valueOf(next.trackColor ?: next.controlColor)
            }
        }
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != next.checked) {
                next.onCheckedChange?.invoke(isChecked)
            }
        }
        if (previous.textColor != next.textColor) {
            view.setTextColor(next.textColor)
        }
        if (previous.textSizeSp != next.textSizeSp) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, next.textSizeSp.toFloat())
        }
    }

    fun applySliderPatch(
        view: SeekBar,
        patch: SliderNodePatch,
    ) {
        val previous = patch.previous
        val next = patch.next
        val listener = view.getTag(R.id.ui_framework_seek_listener) as? SeekBar.OnSeekBarChangeListener
        if (listener != null) {
            view.setOnSeekBarChangeListener(null)
        }
        val resolvedValue = next.value.coerceIn(next.min, next.max)
        if (previous.min != next.min || previous.max != next.max) {
            view.max = (next.max - next.min).coerceAtLeast(0)
        }
        if (previous.value != next.value || previous.min != next.min || previous.max != next.max) {
            view.progress = resolvedValue - next.min
        }
        if (previous.enabled != next.enabled) {
            view.isEnabled = next.enabled
        }
        if (previous.thumbColor != next.thumbColor) {
            view.thumbTintList = ColorStateList.valueOf(next.thumbColor)
        }
        if (previous.trackColor != next.trackColor) {
            view.progressTintList = ColorStateList.valueOf(next.trackColor)
        }
        val nextListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val nextValue = next.min + progress
                if (fromUser && nextValue != resolvedValue) {
                    next.onValueChange?.invoke(nextValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        view.setOnSeekBarChangeListener(nextListener)
        view.setTag(R.id.ui_framework_seek_listener, nextListener)
    }
}
