package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps

fun UiTreeBuilder.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    label: String = "",
    placeholder: String = hint,
    supportingText: String = "",
    singleLine: Boolean = true,
    keyboardType: TextFieldType = TextFieldType.Text,
    readOnly: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = if (singleLine) 1 else 3,
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    style: UiTextStyle = TextFieldDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val hintColor = TextFieldDefaults.hintColor(
        enabled = enabled,
        isError = isError,
    )
    val labelColor = TextFieldDefaults.labelColor(
        enabled = enabled,
        isError = isError,
    )
    val supportingTextColor = TextFieldDefaults.supportingTextColor(
        enabled = enabled,
        isError = isError,
    )
    emit(
        type = NodeType.TextField,
        key = key,
        props = props {
            set(TypedPropKeys.Value, value)
            set(TypedPropKeys.OnValueChange, onValueChange)
            set(TypedPropKeys.Hint, hint)
            set(TypedPropKeys.Label, label)
            set(TypedPropKeys.Placeholder, placeholder)
            set(TypedPropKeys.SupportingText, supportingText)
            set(TypedPropKeys.SingleLine, singleLine)
            set(TypedPropKeys.TextFieldType, keyboardType)
            set(TypedPropKeys.ReadOnly, readOnly)
            set(TypedPropKeys.MaxLines, maxLines)
            set(TypedPropKeys.MinLines, minLines)
            set(TypedPropKeys.ImeAction, imeAction)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.IsError, isError)
            set(TypedPropKeys.TextColor, TextFieldDefaults.textColor(enabled))
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            if (singleLine) {
                set(TypedPropKeys.StyleMinHeight, TextFieldDefaults.height(size))
            }
            set(TypedPropKeys.StylePaddingLeft, TextFieldDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingTop, TextFieldDefaults.verticalPadding(size))
            set(TypedPropKeys.StylePaddingRight, TextFieldDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingBottom, TextFieldDefaults.verticalPadding(size))
            set(
                TypedPropKeys.StyleBackgroundColor,
                TextFieldDefaults.containerColor(
                    variant = variant,
                    enabled = enabled,
                    isError = isError,
                ),
            )
            set(TypedPropKeys.StyleBorderWidth, TextFieldDefaults.borderWidth(variant))
            set(
                TypedPropKeys.StyleBorderColor,
                TextFieldDefaults.borderColor(
                    variant = variant,
                    enabled = enabled,
                    isError = isError,
                ),
            )
            set(TypedPropKeys.StyleCornerRadius, TextFieldDefaults.cornerRadius())
            set(TypedPropKeys.StyleRippleColor, TextFieldDefaults.pressedColor())
            set(TypedPropKeys.HintTextColor, hintColor)
            set(TypedPropKeys.LabelTextColor, labelColor)
            set(TypedPropKeys.SupportingTextColor, supportingTextColor)
            set(TypedPropKeys.LabelTextSizeSp, TextFieldDefaults.labelTextStyle().fontSizeSp)
            set(TypedPropKeys.SupportingTextSizeSp, TextFieldDefaults.supportingTextStyle().fontSizeSp)
        },
        spec = TextFieldNodeProps(
            value = value,
            label = label,
            labelColor = labelColor,
            labelTextSizeSp = TextFieldDefaults.labelTextStyle().fontSizeSp,
            supportingText = supportingText,
            supportingTextColor = supportingTextColor,
            supportingTextSizeSp = TextFieldDefaults.supportingTextStyle().fontSizeSp,
            placeholder = placeholder.ifEmpty { hint },
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardType = keyboardType,
            imeAction = imeAction,
            hintColor = hintColor,
            readOnly = readOnly,
            onValueChange = onValueChange,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    label: String = "",
    supportingText: String = "",
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    style: UiTextStyle = TextFieldDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        label = label,
        supportingText = supportingText,
        imeAction = imeAction,
        singleLine = true,
        keyboardType = TextFieldType.Password,
        variant = variant,
        size = size,
        enabled = enabled,
        isError = isError,
        style = style,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    label: String = "",
    supportingText: String = "",
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    style: UiTextStyle = TextFieldDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        label = label,
        supportingText = supportingText,
        imeAction = imeAction,
        singleLine = true,
        keyboardType = TextFieldType.Email,
        variant = variant,
        size = size,
        enabled = enabled,
        style = style,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    label: String = "",
    supportingText: String = "",
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    style: UiTextStyle = TextFieldDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        label = label,
        supportingText = supportingText,
        imeAction = imeAction,
        singleLine = true,
        keyboardType = TextFieldType.Number,
        variant = variant,
        size = size,
        enabled = enabled,
        style = style,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.TextArea(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    label: String = "",
    placeholder: String = hint,
    supportingText: String = "",
    variant: TextFieldVariant = TextFieldVariant.Filled,
    size: TextFieldSize = TextFieldSize.Medium,
    enabled: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    minLines: Int = 3,
    maxLines: Int = Int.MAX_VALUE,
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    style: UiTextStyle = TextFieldDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
        label = label,
        placeholder = placeholder,
        supportingText = supportingText,
        singleLine = false,
        keyboardType = TextFieldType.Text,
        readOnly = readOnly,
        maxLines = maxLines,
        minLines = minLines,
        imeAction = imeAction,
        variant = variant,
        size = size,
        enabled = enabled,
        isError = isError,
        style = style,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.Checkbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val controlColor = InputControlDefaults.checkboxControlColor(enabled)
    emit(
        type = NodeType.Checkbox,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.Checked, checked)
            set(TypedPropKeys.OnCheckedChange, onCheckedChange)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ControlColor, controlColor)
            set(TypedPropKeys.TextColor, InputControlDefaults.checkboxLabelColor(enabled))
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.StyleRippleColor, InputControlDefaults.pressedColor())
        },
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Switch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val controlColor = InputControlDefaults.switchControlColor(enabled)
    emit(
        type = NodeType.Switch,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.Checked, checked)
            set(TypedPropKeys.OnCheckedChange, onCheckedChange)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ControlColor, controlColor)
            set(TypedPropKeys.TextColor, InputControlDefaults.switchLabelColor(enabled))
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.StyleRippleColor, InputControlDefaults.pressedColor())
        },
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.RadioButton(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val controlColor = InputControlDefaults.radioButtonControlColor(enabled)
    emit(
        type = NodeType.RadioButton,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.Checked, checked)
            set(TypedPropKeys.OnCheckedChange, onCheckedChange)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ControlColor, controlColor)
            set(TypedPropKeys.TextColor, InputControlDefaults.radioButtonLabelColor(enabled))
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.StyleRippleColor, InputControlDefaults.pressedColor())
        },
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = 100,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val controlColor = InputControlDefaults.sliderControlColor(enabled)
    emit(
        type = NodeType.Slider,
        key = key,
        props = props {
            set(TypedPropKeys.SliderValue, value)
            set(TypedPropKeys.MinValue, min)
            set(TypedPropKeys.MaxValue, max)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ControlColor, controlColor)
            set(TypedPropKeys.OnSliderValueChange, onValueChange)
        },
        spec = SliderNodeProps(
            min = min,
            max = max,
            value = value,
            enabled = enabled,
            tintColor = controlColor,
            onValueChange = onValueChange,
        ),
        modifier = modifier,
    )
}
