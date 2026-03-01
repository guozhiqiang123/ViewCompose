package com.gzq.uiframework.renderer.view.tree.patch

import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import com.gzq.uiframework.renderer.view.tree.InputViewBinder
import com.gzq.uiframework.renderer.view.tree.TextFieldNodePatch

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
        if (previous.readOnly != next.readOnly) {
            InputViewBinder.applyReadOnly(input, next.readOnly)
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
    }
}
