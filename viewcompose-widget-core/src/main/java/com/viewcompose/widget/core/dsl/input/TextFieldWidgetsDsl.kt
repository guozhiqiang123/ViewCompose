package com.viewcompose.widget.core

import com.viewcompose.ui.modifier.Modifier
import com.viewcompose.ui.modifier.fillMaxWidth
import com.viewcompose.ui.modifier.margin
import com.viewcompose.ui.node.NodeType
import com.viewcompose.ui.node.TextFieldImeAction
import com.viewcompose.ui.node.TextFieldType
import com.viewcompose.ui.node.spec.TextFieldNodeProps
import com.viewcompose.ui.node.spec.uiFontFamily

fun UiTreeBuilder.BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    placeholder: String = hint,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardType: TextFieldType = TextFieldType.Text,
    readOnly: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = if (singleLine) 1 else 1,
    maxLength: Int? = null,
    imeAction: TextFieldImeAction = TextFieldImeAction.Default,
    cursorColor: Int = TextFieldDefaults.cursorColor(),
    textColor: Int = TextFieldDefaults.textColor(enabled),
    textStyle: UiTextStyle = TextFieldDefaults.textStyle(),
    hintColor: Int = TextFieldDefaults.hintColor(enabled = enabled),
    backgroundColor: Int = 0x00000000,
    borderWidth: Int = 0,
    borderColor: Int = 0x00000000,
    cornerRadius: Int = 0,
    minHeight: Int = 0,
    paddingHorizontal: Int = 0,
    paddingVertical: Int = 0,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.TextField,
        key = key,
        spec = basicTextFieldSpec(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder.ifEmpty { hint },
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            keyboardType = keyboardType,
            imeAction = imeAction,
            hintColor = hintColor,
            readOnly = readOnly,
            textColor = textColor,
            textStyle = textStyle,
            backgroundColor = backgroundColor,
            borderWidth = borderWidth,
            borderColor = borderColor,
            cornerRadius = cornerRadius,
            minHeight = minHeight,
            paddingHorizontal = paddingHorizontal,
            paddingVertical = paddingVertical,
            maxLength = maxLength,
            cursorColor = cursorColor,
        ),
        modifier = modifier,
    )
}

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
    cursorColor: Int = TextFieldDefaults.cursorColor(),
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
    Column(
        key = key,
        modifier = modifier,
    ) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = TextFieldDefaults.labelTextStyle(),
                color = labelColor,
                modifier = Modifier.margin(bottom = 4.dp),
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            placeholder = placeholder.ifEmpty { hint },
            enabled = enabled,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardType = keyboardType,
            imeAction = imeAction,
            hintColor = hintColor,
            readOnly = readOnly,
            textColor = TextFieldDefaults.textColor(
                enabled = enabled,
                isError = isError,
            ),
            textStyle = style,
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
            minHeight = if (singleLine) TextFieldDefaults.height(size) else 0,
            paddingHorizontal = TextFieldDefaults.horizontalPadding(size),
            paddingVertical = TextFieldDefaults.verticalPadding(size),
            maxLength = maxLength,
            cursorColor = cursorColor,
            modifier = Modifier.fillMaxWidth(),
        )
        if (supportingText.isNotBlank()) {
            Text(
                text = supportingText,
                style = TextFieldDefaults.supportingTextStyle(),
                color = supportingTextColor,
                modifier = Modifier.margin(top = 4.dp),
            )
        }
    }
}

private fun basicTextFieldSpec(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    singleLine: Boolean,
    minLines: Int,
    maxLines: Int,
    keyboardType: TextFieldType,
    imeAction: TextFieldImeAction,
    hintColor: Int,
    readOnly: Boolean,
    textColor: Int,
    textStyle: UiTextStyle,
    backgroundColor: Int,
    borderWidth: Int,
    borderColor: Int,
    cornerRadius: Int,
    minHeight: Int,
    paddingHorizontal: Int,
    paddingVertical: Int,
    maxLength: Int?,
    cursorColor: Int,
): TextFieldNodeProps {
    return TextFieldNodeProps(
        value = value,
        placeholder = placeholder,
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        keyboardType = keyboardType,
        imeAction = imeAction,
        hintColor = hintColor,
        readOnly = readOnly,
        onValueChange = onValueChange,
        textColor = textColor,
        textSizeSp = textStyle.fontSizeSp,
        fontWeight = textStyle.fontWeight,
        fontFamily = uiFontFamily(textStyle.fontFamily),
        letterSpacingEm = textStyle.letterSpacingEm,
        lineHeightSp = textStyle.lineHeightSp,
        includeFontPadding = textStyle.includeFontPadding,
        backgroundColor = backgroundColor,
        borderWidth = borderWidth,
        borderColor = borderColor,
        cornerRadius = cornerRadius,
        minHeight = minHeight,
        paddingHorizontal = paddingHorizontal,
        paddingVertical = paddingVertical,
        maxLength = maxLength,
        cursorColor = cursorColor,
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
