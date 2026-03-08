package com.viewcompose.renderer.view.tree

import android.content.res.ColorStateList
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import com.viewcompose.renderer.R
import com.viewcompose.renderer.node.TextFieldImeAction
import com.viewcompose.renderer.node.TextFieldType
import com.viewcompose.renderer.node.VNode
import com.viewcompose.renderer.node.spec.SliderNodeProps
import com.viewcompose.renderer.node.spec.TextFieldNodeProps
import com.viewcompose.renderer.node.spec.ToggleNodeProps
import com.viewcompose.renderer.view.container.DeclarativeTextFieldLayout

internal object InputViewBinder {
    data class TextFieldSpec(
        val value: String,
        val label: String,
        val labelColor: Int,
        val labelTextSizeSp: Int,
        val supportingText: String,
        val supportingTextColor: Int,
        val supportingTextSizeSp: Int,
        val placeholder: String,
        val enabled: Boolean,
        val singleLine: Boolean,
        val minLines: Int,
        val maxLines: Int,
        val inputType: Int,
        val imeAction: Int,
        val hintColor: Int,
        val readOnly: Boolean,
        val onValueChange: ((String) -> Unit)?,
        val maxLength: Int? = null,
        val cursorColor: Int = 0,
    )

    data class ToggleSpec(
        val text: CharSequence?,
        val enabled: Boolean,
        val checked: Boolean,
        val controlColor: Int,
        val thumbColor: Int? = null,
        val trackColor: Int? = null,
        val checkedColor: Int? = null,
        val uncheckedColor: Int? = null,
        val onCheckedChange: ((Boolean) -> Unit)?,
    )

    data class SliderSpec(
        val min: Int,
        val max: Int,
        val value: Int,
        val enabled: Boolean,
        val thumbColor: Int,
        val trackColor: Int,
        val onValueChange: ((Int) -> Unit)?,
    )

    fun bindTextField(
        view: DeclarativeTextFieldLayout,
        spec: TextFieldSpec,
    ) {
        val input = view.inputView
        if (input.text?.toString() != spec.value) {
            input.setText(spec.value)
            input.setSelection(spec.value.length)
        }
        view.setLabel(
            text = spec.label,
            color = spec.labelColor,
            textSizeSp = spec.labelTextSizeSp,
        )
        view.setSupportingText(
            text = spec.supportingText,
            color = spec.supportingTextColor,
            textSizeSp = spec.supportingTextSizeSp,
        )
        input.hint = spec.placeholder
        input.isEnabled = spec.enabled
        input.isSingleLine = spec.singleLine
        input.minLines = if (spec.singleLine) 1 else spec.minLines
        input.maxLines = if (spec.singleLine) 1 else spec.maxLines
        input.inputType = spec.inputType
        input.imeOptions = spec.imeAction
        input.setHintTextColor(spec.hintColor)
        if (spec.cursorColor != 0) {
            input.highlightColor = spec.cursorColor
        }
        applyMaxLength(input, spec.maxLength)
        applyReadOnly(input, spec.readOnly)
        bindTextWatcher(
            view = input,
            currentValue = spec.value,
            onValueChange = spec.onValueChange,
        )
    }

    fun bindCheckbox(
        view: CheckBox,
        spec: ToggleSpec,
    ) {
        bindCompoundButton(
            view = view,
            spec = spec,
        )
    }

    fun bindSwitch(
        view: Switch,
        spec: ToggleSpec,
    ) {
        bindCompoundButton(
            view = view,
            spec = spec,
        )
    }

    fun bindRadioButton(
        view: RadioButton,
        spec: ToggleSpec,
    ) {
        bindCompoundButton(
            view = view,
            spec = spec,
        )
    }

    fun bindSlider(
        view: SeekBar,
        spec: SliderSpec,
    ) {
        val listener = view.getTag(R.id.ui_framework_seek_listener) as? SeekBar.OnSeekBarChangeListener
        if (listener != null) {
            view.setOnSeekBarChangeListener(null)
        }
        val resolvedValue = spec.value.coerceIn(spec.min, spec.max)
        view.max = (spec.max - spec.min).coerceAtLeast(0)
        view.progress = resolvedValue - spec.min
        view.isEnabled = spec.enabled
        view.progressTintList = ColorStateList.valueOf(spec.trackColor)
        view.thumbTintList = ColorStateList.valueOf(spec.thumbColor)
        val nextListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val nextValue = spec.min + progress
                if (fromUser && nextValue != resolvedValue) {
                    spec.onValueChange?.invoke(nextValue)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        view.setOnSeekBarChangeListener(nextListener)
        view.setTag(R.id.ui_framework_seek_listener, nextListener)
    }

    private fun bindCompoundButton(
        view: CompoundButton,
        spec: ToggleSpec,
    ) {
        view.setOnCheckedChangeListener(null)
        view.text = spec.text
        view.isEnabled = spec.enabled
        view.isChecked = spec.checked
        if (view is Switch) {
            view.buttonTintList = ColorStateList.valueOf(spec.controlColor)
            view.thumbTintList = ColorStateList.valueOf(spec.thumbColor ?: spec.controlColor)
            view.trackTintList = ColorStateList.valueOf(spec.trackColor ?: spec.controlColor)
        } else if (spec.checkedColor != null || spec.uncheckedColor != null) {
            val checked = spec.checkedColor ?: spec.controlColor
            val unchecked = spec.uncheckedColor ?: spec.controlColor
            view.buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked),
                ),
                intArrayOf(checked, unchecked),
            )
        } else {
            view.buttonTintList = ColorStateList.valueOf(spec.controlColor)
        }
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != spec.checked) {
                spec.onCheckedChange?.invoke(isChecked)
            }
        }
    }

    fun readTextFieldSpec(node: VNode): TextFieldSpec {
        val spec = node.requireSpec<TextFieldNodeProps>()
        return TextFieldSpec(
            value = spec.value,
            label = spec.label,
            labelColor = spec.labelColor,
            labelTextSizeSp = spec.labelTextSizeSp,
            supportingText = spec.supportingText,
            supportingTextColor = spec.supportingTextColor,
            supportingTextSizeSp = spec.supportingTextSizeSp,
            placeholder = spec.placeholder,
            enabled = spec.enabled,
            singleLine = spec.singleLine,
            minLines = spec.minLines,
            maxLines = spec.maxLines,
            inputType = resolveInputType(
                type = spec.keyboardType,
                singleLine = spec.singleLine,
            ),
            imeAction = spec.imeAction.toEditorAction(),
            hintColor = spec.hintColor,
            readOnly = spec.readOnly,
            onValueChange = spec.onValueChange,
            maxLength = spec.maxLength,
            cursorColor = spec.cursorColor,
        )
    }

    fun readToggleSpec(node: VNode): ToggleSpec {
        val spec = node.requireSpec<ToggleNodeProps>()
        return ToggleSpec(
            text = spec.text,
            enabled = spec.enabled,
            checked = spec.checked,
            controlColor = spec.controlColor,
            thumbColor = spec.thumbColor,
            trackColor = spec.trackColor,
            checkedColor = spec.checkedColor,
            uncheckedColor = spec.uncheckedColor,
            onCheckedChange = spec.onCheckedChange,
        )
    }

    fun readSliderSpec(node: VNode): SliderSpec {
        val spec = node.requireSpec<SliderNodeProps>()
        return SliderSpec(
            min = spec.min,
            max = spec.max,
            value = spec.value,
            enabled = spec.enabled,
            thumbColor = spec.thumbColor,
            trackColor = spec.trackColor,
            onValueChange = spec.onValueChange,
        )
    }

    internal fun resolveInputType(type: TextFieldType, singleLine: Boolean): Int {
        val baseType = when (type) {
            TextFieldType.Text -> InputType.TYPE_CLASS_TEXT
            TextFieldType.Password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            TextFieldType.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TextFieldType.Number -> InputType.TYPE_CLASS_NUMBER
        }
        return if (singleLine) baseType else baseType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    }

    internal fun toEditorAction(action: TextFieldImeAction): Int {
        return action.toEditorAction()
    }

    internal fun bindTextWatcher(
        view: EditText,
        currentValue: String,
        onValueChange: ((String) -> Unit)?,
    ) {
        attachTextWatcher(
            view = view,
            currentValue = currentValue,
            onValueChange = onValueChange,
        )
    }

    internal fun applyReadOnly(
        view: EditText,
        readOnly: Boolean,
    ) {
        updateReadOnly(
            view = view,
            readOnly = readOnly,
        )
    }

    internal fun applyMaxLength(
        view: EditText,
        maxLength: Int?,
    ) {
        val existing = view.filters.filterNot { it is InputFilter.LengthFilter }
        view.filters = if (maxLength != null && maxLength > 0) {
            (existing + InputFilter.LengthFilter(maxLength)).toTypedArray()
        } else {
            existing.toTypedArray()
        }
    }

    private fun TextFieldImeAction.toEditorAction(): Int {
        return when (this) {
            TextFieldImeAction.Default -> EditorInfo.IME_ACTION_UNSPECIFIED
            TextFieldImeAction.Next -> EditorInfo.IME_ACTION_NEXT
            TextFieldImeAction.Done -> EditorInfo.IME_ACTION_DONE
            TextFieldImeAction.Go -> EditorInfo.IME_ACTION_GO
            TextFieldImeAction.Search -> EditorInfo.IME_ACTION_SEARCH
            TextFieldImeAction.Send -> EditorInfo.IME_ACTION_SEND
        }
    }

    private fun attachTextWatcher(
        view: EditText,
        currentValue: String,
        onValueChange: ((String) -> Unit)?,
    ) {
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
                if (nextValue != currentValue) {
                    onValueChange?.invoke(nextValue)
                }
            }
        }
        view.addTextChangedListener(watcher)
        view.setTag(R.id.ui_framework_text_watcher, watcher)
    }

    private fun updateReadOnly(
        view: EditText,
        readOnly: Boolean,
    ) {
        view.isFocusable = !readOnly
        view.isFocusableInTouchMode = !readOnly
        view.isCursorVisible = !readOnly
        view.isLongClickable = !readOnly
        view.setTextIsSelectable(readOnly)
    }
}
