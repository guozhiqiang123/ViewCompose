package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
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
    maxLength: Int? = null,
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
            textColor = TextFieldDefaults.textColor(enabled),
            textSizeSp = style.fontSizeSp,
            backgroundColor = TextFieldDefaults.containerColor(
                variant = variant,
                enabled = enabled,
                isError = isError,
            ),
            borderWidth = TextFieldDefaults.borderWidth(variant),
            borderColor = TextFieldDefaults.borderColor(
                variant = variant,
                enabled = enabled,
                isError = isError,
            ),
            cornerRadius = TextFieldDefaults.cornerRadius(),
            rippleColor = TextFieldDefaults.pressedColor(),
            minHeight = if (singleLine) TextFieldDefaults.height(size) else 0,
            paddingHorizontal = TextFieldDefaults.horizontalPadding(size),
            paddingVertical = TextFieldDefaults.verticalPadding(size),
            maxLength = maxLength,
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
    maxLength: Int? = null,
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
        maxLength = maxLength,
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
    maxLength: Int? = null,
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
        maxLength = maxLength,
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
    maxLength: Int? = null,
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
        maxLength = maxLength,
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
    maxLength: Int? = null,
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
        maxLength = maxLength,
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
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
            textColor = InputControlDefaults.checkboxLabelColor(enabled),
            textSizeSp = style.fontSizeSp,
            rippleColor = InputControlDefaults.pressedColor(),
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Switch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    thumbColor: Int? = null,
    trackColor: Int? = null,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val controlColor = InputControlDefaults.switchControlColor(enabled)
    emit(
        type = NodeType.Switch,
        key = key,
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            thumbColor = thumbColor,
            trackColor = trackColor,
            onCheckedChange = onCheckedChange,
            textColor = InputControlDefaults.switchLabelColor(enabled),
            textSizeSp = style.fontSizeSp,
            rippleColor = InputControlDefaults.pressedColor(),
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
        spec = ToggleNodeProps(
            text = text,
            enabled = enabled,
            checked = checked,
            controlColor = controlColor,
            onCheckedChange = onCheckedChange,
            textColor = InputControlDefaults.radioButtonLabelColor(enabled),
            textSizeSp = style.fontSizeSp,
            rippleColor = InputControlDefaults.pressedColor(),
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
    thumbColor: Int = InputControlDefaults.sliderThumbColor(enabled),
    trackColor: Int = InputControlDefaults.sliderTrackColor(enabled),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Slider,
        key = key,
        spec = SliderNodeProps(
            min = min,
            max = max,
            value = value,
            enabled = enabled,
            thumbColor = thumbColor,
            trackColor = trackColor,
            onValueChange = onValueChange,
        ),
        modifier = modifier,
    )
}
