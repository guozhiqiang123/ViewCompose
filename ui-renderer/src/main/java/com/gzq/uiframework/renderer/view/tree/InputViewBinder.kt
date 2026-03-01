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
import com.gzq.uiframework.renderer.view.container.DeclarativeTextFieldLayout

internal object InputViewBinder {
    fun bindTextField(
        view: DeclarativeTextFieldLayout,
        value: String,
        label: String,
        labelColor: Int,
        labelTextSizeSp: Int,
        supportingText: String,
        supportingTextColor: Int,
        supportingTextSizeSp: Int,
        placeholder: String,
        enabled: Boolean,
        singleLine: Boolean,
        minLines: Int,
        maxLines: Int,
        inputType: Int,
        imeAction: Int,
        hintColor: Int,
        readOnly: Boolean,
        onValueChange: ((String) -> Unit)?,
    ) {
        val input = view.inputView
        if (input.text?.toString() != value) {
            input.setText(value)
            input.setSelection(value.length)
        }
        view.setLabel(
            text = label,
            color = labelColor,
            textSizeSp = labelTextSizeSp,
        )
        view.setSupportingText(
            text = supportingText,
            color = supportingTextColor,
            textSizeSp = supportingTextSizeSp,
        )
        input.hint = placeholder
        input.isEnabled = enabled
        input.isSingleLine = singleLine
        input.minLines = if (singleLine) 1 else minLines
        input.maxLines = if (singleLine) 1 else maxLines
        input.inputType = inputType
        input.imeOptions = imeAction
        input.setHintTextColor(hintColor)
        applyReadOnly(input, readOnly)
        bindTextWatcher(
            view = input,
            currentValue = value,
            onValueChange = onValueChange,
        )
    }

    fun bindCheckbox(
        view: CheckBox,
        text: CharSequence?,
        enabled: Boolean,
        checked: Boolean,
        controlColor: Int,
        onCheckedChange: ((Boolean) -> Unit)?,
    ) {
        bindCompoundButton(
            view = view,
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        )
    }

    fun bindSwitch(
        view: Switch,
        text: CharSequence?,
        enabled: Boolean,
        checked: Boolean,
        controlColor: Int,
        onCheckedChange: ((Boolean) -> Unit)?,
    ) {
        bindCompoundButton(
            view = view,
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        )
    }

    fun bindRadioButton(
        view: RadioButton,
        text: CharSequence?,
        enabled: Boolean,
        checked: Boolean,
        controlColor: Int,
        onCheckedChange: ((Boolean) -> Unit)?,
    ) {
        bindCompoundButton(
            view = view,
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        )
    }

    fun bindSlider(
        view: SeekBar,
        min: Int,
        max: Int,
        value: Int,
        enabled: Boolean,
        tintColor: Int,
        onValueChange: ((Int) -> Unit)?,
    ) {
        val listener = view.getTag(R.id.ui_framework_seek_listener) as? SeekBar.OnSeekBarChangeListener
        if (listener != null) {
            view.setOnSeekBarChangeListener(null)
        }
        val resolvedValue = value.coerceIn(min, max)
        view.max = (max - min).coerceAtLeast(0)
        view.progress = resolvedValue - min
        view.isEnabled = enabled
        val tint = ColorStateList.valueOf(tintColor)
        view.progressTintList = tint
        view.thumbTintList = tint
        val nextListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val nextValue = min + progress
                if (fromUser && nextValue != resolvedValue) {
                    onValueChange?.invoke(nextValue)
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
        text: CharSequence?,
        enabled: Boolean,
        checked: Boolean,
        controlColor: Int,
        onCheckedChange: ((Boolean) -> Unit)?,
    ) {
        view.setOnCheckedChangeListener(null)
        view.text = text
        view.isEnabled = enabled
        view.isChecked = checked
        val tint = ColorStateList.valueOf(controlColor)
        view.buttonTintList = tint
        if (view is Switch) {
            view.thumbTintList = tint
            view.trackTintList = tint
        }
        view.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != checked) {
                onCheckedChange?.invoke(isChecked)
            }
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
