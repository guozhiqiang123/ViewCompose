package com.gzq.uiframework.renderer.view.tree

import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.SeekBar
import android.widget.Switch
import com.gzq.uiframework.renderer.R
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.VNode
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout
import android.text.InputType
import android.view.inputmethod.EditorInfo

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
    )

    data class ToggleSpec(
        val text: CharSequence?,
        val enabled: Boolean,
        val checked: Boolean,
        val controlColor: Int,
        val onCheckedChange: ((Boolean) -> Unit)?,
    )

    data class SliderSpec(
        val min: Int,
        val max: Int,
        val value: Int,
        val enabled: Boolean,
        val tintColor: Int,
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
        val tint = ColorStateList.valueOf(spec.tintColor)
        view.progressTintList = tint
        view.thumbTintList = tint
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
        val tint = ColorStateList.valueOf(spec.controlColor)
        view.buttonTintList = tint
        if (view is Switch) {
            view.thumbTintList = tint
            view.trackTintList = tint
        }
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != spec.checked) {
                spec.onCheckedChange?.invoke(isChecked)
            }
        }
    }

    fun readTextFieldSpec(node: VNode): TextFieldSpec {
        val spec = node.spec as? TextFieldNodeProps
        if (spec != null) {
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
            )
        }
        val hintColor = node.props[TypedPropKeys.HintTextColor] ?: 0xFF888888.toInt()
        val singleLine = node.props[TypedPropKeys.SingleLine] ?: true
        return TextFieldSpec(
            value = node.props[TypedPropKeys.Value] ?: "",
            label = node.props[TypedPropKeys.Label] ?: "",
            labelColor = node.props[TypedPropKeys.LabelTextColor] ?: hintColor,
            labelTextSizeSp = node.props[TypedPropKeys.LabelTextSizeSp] ?: 12,
            supportingText = node.props[TypedPropKeys.SupportingText] ?: "",
            supportingTextColor = node.props[TypedPropKeys.SupportingTextColor] ?: hintColor,
            supportingTextSizeSp = node.props[TypedPropKeys.SupportingTextSizeSp] ?: 12,
            placeholder = node.props[TypedPropKeys.Placeholder]
                ?: (node.props[TypedPropKeys.Hint] ?: ""),
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            singleLine = singleLine,
            minLines = node.props[TypedPropKeys.MinLines] ?: 1,
            maxLines = node.props[TypedPropKeys.MaxLines] ?: Int.MAX_VALUE,
            inputType = resolveInputType(
                type = node.props[TypedPropKeys.TextFieldType] ?: TextFieldType.Text,
                singleLine = singleLine,
            ),
            imeAction = (node.props[TypedPropKeys.ImeAction]
                ?: TextFieldImeAction.Default).toEditorAction(),
            hintColor = hintColor,
            readOnly = node.props[TypedPropKeys.ReadOnly] ?: false,
            onValueChange = node.props[TypedPropKeys.OnValueChange],
        )
    }

    fun readToggleSpec(node: VNode): ToggleSpec {
        return ToggleSpec(
            text = node.props[TypedPropKeys.Text],
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            checked = node.props[TypedPropKeys.Checked] ?: false,
            controlColor = node.props[TypedPropKeys.ControlColor] ?: 0xFF000000.toInt(),
            onCheckedChange = node.props[TypedPropKeys.OnCheckedChange],
        )
    }

    fun readSliderSpec(node: VNode): SliderSpec {
        return SliderSpec(
            min = node.props[TypedPropKeys.MinValue] ?: 0,
            max = node.props[TypedPropKeys.MaxValue] ?: 100,
            value = node.props[TypedPropKeys.SliderValue] ?: 0,
            enabled = node.props[TypedPropKeys.Enabled] ?: true,
            tintColor = node.props[TypedPropKeys.ControlColor] ?: 0xFF000000.toInt(),
            onValueChange = node.props[TypedPropKeys.OnSliderValueChange],
        )
    }

    private fun resolveInputType(type: TextFieldType, singleLine: Boolean): Int {
        val baseType = when (type) {
            TextFieldType.Text -> InputType.TYPE_CLASS_TEXT
            TextFieldType.Password -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            TextFieldType.Email -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            TextFieldType.Number -> InputType.TYPE_CLASS_NUMBER
        }
        return if (singleLine) baseType else baseType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
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

    private fun bindTextWatcher(
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

    private fun applyReadOnly(
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
