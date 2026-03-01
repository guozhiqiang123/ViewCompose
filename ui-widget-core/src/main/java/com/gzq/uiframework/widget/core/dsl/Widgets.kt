package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.WeightModifierElement
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.TypedPropKeys
import com.gzq.uiframework.renderer.node.props
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TextOverflow
import com.gzq.uiframework.renderer.node.spec.ButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.AndroidViewNodeProps
import com.gzq.uiframework.renderer.node.spec.BoxNodeProps
import com.gzq.uiframework.renderer.node.spec.ColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.DividerNodeProps
import com.gzq.uiframework.renderer.node.spec.IconButtonNodeProps
import com.gzq.uiframework.renderer.node.spec.ImageNodeProps
import com.gzq.uiframework.renderer.node.spec.LazyColumnNodeProps
import com.gzq.uiframework.renderer.node.spec.ProgressIndicatorNodeProps
import com.gzq.uiframework.renderer.node.spec.RowNodeProps
import com.gzq.uiframework.renderer.node.spec.SegmentedControlNodeProps
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps
import com.gzq.uiframework.renderer.node.spec.TextNodeProps
import com.gzq.uiframework.renderer.node.spec.TabPagerNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps

fun UiTreeBuilder.Text(
    text: String,
    style: UiTextStyle = TextDefaults.bodyStyle(),
    color: Int = TextDefaults.primaryColor(),
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Text,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.TextColor, color)
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.TextMaxLines, maxLines)
            set(TypedPropKeys.TextOverflow, overflow)
            set(TypedPropKeys.TextAlign, textAlign)
        },
        spec = TextNodeProps(
            text = text,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Image(
    source: ImageSource,
    contentDescription: String? = null,
    contentScale: ImageContentScale = ImageContentScale.Fit,
    tint: Int? = null,
    placeholder: ImageSource.Resource? = null,
    error: ImageSource.Resource? = placeholder,
    fallback: ImageSource.Resource? = placeholder,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Image,
        key = key,
        props = props {
            set(TypedPropKeys.ImageSource, source)
            set(TypedPropKeys.ImageContentScale, contentScale)
            set(TypedPropKeys.ImageContentDescription, contentDescription)
            set(TypedPropKeys.ImageRemoteLoader, ImageLoading.current)
            set(TypedPropKeys.ImageTint, tint)
            set(TypedPropKeys.ImagePlaceholder, placeholder)
            set(TypedPropKeys.ImageError, error)
            set(TypedPropKeys.ImageFallback, fallback)
        },
        spec = ImageNodeProps(
            contentDescription = contentDescription,
            contentScale = contentScale,
            tint = tint,
            source = source,
            placeholder = placeholder,
            error = error,
            fallback = fallback,
            remoteImageLoader = ImageLoading.current,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Icon(
    source: ImageSource,
    contentDescription: String? = null,
    tint: Int = ContentColor.current,
    size: Int = 24.dp,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    Image(
        source = source,
        contentDescription = contentDescription,
        contentScale = ImageContentScale.Inside,
        tint = tint,
        key = key,
        modifier = Modifier
            .size(width = size, height = size)
            .then(modifier),
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

fun UiTreeBuilder.LinearProgressIndicator(
    progress: Float? = null,
    indicatorColor: Int = ProgressIndicatorDefaults.linearIndicatorColor(),
    trackColor: Int = ProgressIndicatorDefaults.linearTrackColor(),
    trackThickness: Int = ProgressIndicatorDefaults.linearTrackThickness(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.LinearProgressIndicator,
        key = key,
        props = props {
            set(TypedPropKeys.ProgressFraction, progress)
            set(TypedPropKeys.ProgressIndicatorColor, indicatorColor)
            set(TypedPropKeys.ProgressTrackColor, trackColor)
            set(TypedPropKeys.ProgressTrackThickness, trackThickness)
        },
        spec = ProgressIndicatorNodeProps(
            enabled = true,
            progress = progress,
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            trackThickness = trackThickness,
            indicatorSize = 0,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(trackThickness)
            .then(modifier),
    )
}

fun UiTreeBuilder.CircularProgressIndicator(
    progress: Float? = null,
    indicatorColor: Int = ProgressIndicatorDefaults.circularIndicatorColor(),
    trackColor: Int = ProgressIndicatorDefaults.circularTrackColor(),
    size: Int = ProgressIndicatorDefaults.circularSize(),
    trackThickness: Int = ProgressIndicatorDefaults.circularTrackThickness(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.CircularProgressIndicator,
        key = key,
        props = props {
            set(TypedPropKeys.ProgressFraction, progress)
            set(TypedPropKeys.ProgressIndicatorColor, indicatorColor)
            set(TypedPropKeys.ProgressTrackColor, trackColor)
            set(TypedPropKeys.ProgressTrackThickness, trackThickness)
            set(TypedPropKeys.ProgressIndicatorSize, size)
        },
        spec = ProgressIndicatorNodeProps(
            enabled = true,
            progress = progress,
            indicatorColor = indicatorColor,
            trackColor = trackColor,
            trackThickness = trackThickness,
            indicatorSize = size,
        ),
        modifier = Modifier
            .size(width = size, height = size)
            .then(modifier),
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

fun UiTreeBuilder.Button(
    text: String,
    onClick: (() -> Unit)? = null,
    leadingIcon: ImageSource.Resource? = null,
    trailingIcon: ImageSource.Resource? = null,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    style: UiTextStyle = ButtonDefaults.textStyle(size),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val contentColor = ButtonDefaults.contentColor(variant, enabled)
    val iconSizeValue = ButtonDefaults.iconSize(size)
    val iconSpacingValue = ButtonDefaults.iconSpacing(size)
    emit(
        type = NodeType.Button,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.OnClick, onClick)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.TextColor, contentColor)
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
            set(TypedPropKeys.StyleMinHeight, ButtonDefaults.height(size))
            set(TypedPropKeys.StylePaddingLeft, ButtonDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingTop, ButtonDefaults.verticalPadding(size))
            set(TypedPropKeys.StylePaddingRight, ButtonDefaults.horizontalPadding(size))
            set(TypedPropKeys.StylePaddingBottom, ButtonDefaults.verticalPadding(size))
            set(TypedPropKeys.StyleBackgroundColor, ButtonDefaults.containerColor(variant, enabled))
            set(TypedPropKeys.StyleBorderWidth, ButtonDefaults.borderWidth(variant))
            set(TypedPropKeys.StyleBorderColor, ButtonDefaults.borderColor(variant, enabled))
            set(TypedPropKeys.StyleCornerRadius, ButtonDefaults.cornerRadius())
            set(TypedPropKeys.StyleRippleColor, ButtonDefaults.pressedColor())
            set(TypedPropKeys.ButtonIconSize, iconSizeValue)
            set(TypedPropKeys.ButtonIconSpacing, iconSpacingValue)
            set(TypedPropKeys.ButtonLeadingIcon, leadingIcon)
            set(TypedPropKeys.ButtonTrailingIcon, trailingIcon)
        },
        spec = ButtonNodeProps(
            text = text,
            enabled = enabled,
            iconSpacing = iconSpacingValue,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            iconTint = contentColor,
            iconSize = iconSizeValue,
            onClick = onClick,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.IconButton(
    icon: ImageSource,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val tint = IconButtonDefaults.contentColor(variant, enabled)
    val semanticModifier = Modifier
        .size(
            width = IconButtonDefaults.size(size),
            height = IconButtonDefaults.size(size),
        )
        .then(
            if (enabled && onClick != null) {
                Modifier.clickable(onClick)
            } else {
                Modifier
            },
        )
        .then(modifier)
    emit(
        type = NodeType.IconButton,
        key = key,
        props = props {
            set(TypedPropKeys.ImageSource, icon)
            set(TypedPropKeys.ImageContentDescription, contentDescription)
            set(TypedPropKeys.ImageContentScale, ImageContentScale.Inside)
            set(TypedPropKeys.ImageTint, tint)
            set(TypedPropKeys.OnClick, onClick)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.ImageRemoteLoader, ImageLoading.current)
            set(TypedPropKeys.StylePaddingLeft, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingTop, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingRight, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StylePaddingBottom, IconButtonDefaults.contentPadding(size))
            set(TypedPropKeys.StyleBackgroundColor, IconButtonDefaults.containerColor(variant, enabled))
            set(TypedPropKeys.StyleBorderWidth, IconButtonDefaults.borderWidth(variant))
            set(TypedPropKeys.StyleBorderColor, IconButtonDefaults.borderColor(variant, enabled))
            set(TypedPropKeys.StyleCornerRadius, IconButtonDefaults.cornerRadius())
            set(TypedPropKeys.StyleRippleColor, IconButtonDefaults.pressedColor())
        },
        spec = IconButtonNodeProps(
            contentDescription = contentDescription,
            contentScale = ImageContentScale.Inside,
            tint = tint,
            source = icon,
            placeholder = null,
            error = null,
            fallback = null,
            remoteImageLoader = ImageLoading.current,
            enabled = enabled,
        ),
        modifier = semanticModifier,
    )
}

fun UiTreeBuilder.SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    size: SegmentedControlSize = SegmentedControlSize.Medium,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val resolvedItems = items.map { label -> SegmentedControlItem(label = label) }
    val backgroundColor = SegmentedControlDefaults.backgroundColor(enabled)
    val indicatorColor = SegmentedControlDefaults.indicatorColor(enabled)
    val cornerRadius = SegmentedControlDefaults.cornerRadius()
    val textColor = SegmentedControlDefaults.textColor(enabled)
    val selectedTextColor = SegmentedControlDefaults.selectedTextColor(enabled)
    val rippleColor = SegmentedControlDefaults.rippleColor(enabled)
    val textSizeSp = SegmentedControlDefaults.textStyle(size).fontSizeSp
    val horizontalPadding = SegmentedControlDefaults.horizontalPadding(size)
    val verticalPadding = SegmentedControlDefaults.verticalPadding(size)
    emit(
        type = NodeType.SegmentedControl,
        key = key,
        props = props {
            set(TypedPropKeys.SegmentItems, resolvedItems)
            set(TypedPropKeys.SegmentSelectedIndex, selectedIndex)
            set(TypedPropKeys.OnSegmentSelected, onSelectionChange)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.SegmentBackgroundColor, backgroundColor)
            set(TypedPropKeys.SegmentIndicatorColor, indicatorColor)
            set(TypedPropKeys.SegmentCornerRadius, cornerRadius)
            set(TypedPropKeys.SegmentTextColor, textColor)
            set(TypedPropKeys.SegmentSelectedTextColor, selectedTextColor)
            set(TypedPropKeys.SegmentRippleColor, rippleColor)
            set(TypedPropKeys.SegmentTextSizeSp, textSizeSp)
            set(TypedPropKeys.SegmentContentPaddingHorizontal, horizontalPadding)
            set(TypedPropKeys.SegmentContentPaddingVertical, verticalPadding)
        },
        spec = SegmentedControlNodeProps(
            items = resolvedItems,
            selectedIndex = selectedIndex,
            onSelectionChange = onSelectionChange,
            enabled = enabled,
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            textColor = textColor,
            selectedTextColor = selectedTextColor,
            rippleColor = rippleColor,
            textSizeSp = textSizeSp,
            horizontalPadding = horizontalPadding,
            verticalPadding = verticalPadding,
        ),
        modifier = Modifier
            .height(SegmentedControlDefaults.height(size))
            .then(modifier),
    )
}

fun UiTreeBuilder.AndroidView(
    factory: (Context) -> View,
    update: (View) -> Unit = {},
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.AndroidView,
        key = key,
        props = props {
            set(TypedPropKeys.ViewFactory, factory)
            set(TypedPropKeys.ViewUpdate, update)
        },
        spec = AndroidViewNodeProps(
            factory = factory,
            update = update,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Snackbar(
    visible: Boolean,
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    requestKey: String = "snackbar",
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    if (!visible) {
        return
    }
    submitOverlayRequest(
        OverlayRequest(
            key = requestKey,
            type = OverlayType.Snackbar,
            payload = SnackbarOverlaySpec(
                message = message,
                actionLabel = actionLabel,
                duration = duration,
                onAction = onAction,
                onDismiss = onDismiss,
            ),
        ),
    )
}

fun UiTreeBuilder.Toast(
    visible: Boolean,
    message: String,
    duration: ToastDuration = ToastDuration.Short,
    requestKey: String = "toast",
    onDismiss: (() -> Unit)? = null,
) {
    if (!visible) {
        return
    }
    submitOverlayRequest(
        OverlayRequest(
            key = requestKey,
            type = OverlayType.Toast,
            payload = ToastOverlaySpec(
                message = message,
                duration = duration,
                onDismiss = onDismiss,
            ),
        ),
    )
}

fun UiTreeBuilder.Dialog(
    visible: Boolean,
    requestKey: String = "dialog",
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    onDismissRequest: (() -> Unit)? = null,
    content: UiTreeBuilder.() -> Unit,
) {
    if (!visible) {
        return
    }
    submitOverlayRequest(
        OverlayRequest(
            key = requestKey,
            type = OverlayType.Dialog,
            payload = DialogOverlaySpec(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside,
                onDismissRequest = onDismissRequest,
            ),
            contentToken = DialogOverlayContent(
                nodes = buildVNodeTree(content),
            ),
        ),
    )
}

fun UiTreeBuilder.Box(
    key: Any? = null,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Box,
        key = key,
        props = props {
            set(TypedPropKeys.BoxAlignment, contentAlignment)
        },
        spec = BoxNodeProps(
            contentAlignment = contentAlignment,
        ),
        modifier = modifier,
        children = BoxScope().apply(content).build(),
    )
}

fun UiTreeBuilder.Surface(
    key: Any? = null,
    variant: SurfaceVariant = SurfaceVariant.Default,
    enabled: Boolean = true,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    contentColor: Int = SurfaceDefaults.contentColor(variant),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: BoxScope.() -> Unit,
) {
    val semanticModifier = Modifier
        .then(
            if (enabled && onClick != null) {
                Modifier.clickable(onClick)
            } else {
                Modifier
            },
        )
        .then(modifier)
    ProvideContentColor(contentColor) {
        emitResolved(
            type = NodeType.Surface,
            key = key,
            props = props {
                set(TypedPropKeys.BoxAlignment, contentAlignment)
                set(TypedPropKeys.StyleBackgroundColor, SurfaceDefaults.backgroundColor(variant))
                set(TypedPropKeys.StyleCornerRadius, SurfaceDefaults.cardCornerRadius())
                set(TypedPropKeys.StyleRippleColor, SurfaceDefaults.pressedColor())
                if (!enabled) {
                    set(TypedPropKeys.StyleAlpha, SurfaceDefaults.disabledAlpha())
                }
            },
            spec = BoxNodeProps(
                contentAlignment = contentAlignment,
            ),
            modifier = semanticModifier,
            children = BoxScope().apply(content).build(),
        )
    }
}

fun UiTreeBuilder.Spacer(
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Spacer,
        key = key,
        modifier = modifier,
    )
}

@Deprecated(
    message = "FlexibleSpacer is parent-data. Prefer RowScope.FlexibleSpacer(...) or ColumnScope.FlexibleSpacer(...).",
)
fun UiTreeBuilder.FlexibleSpacer(
    weight: Float = 1f,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    require(weight > 0f) {
        "weight must be > 0"
    }
    Spacer(
        key = key,
        modifier = modifier.then(WeightModifierElement(weight)),
    )
}

fun UiTreeBuilder.Divider(
    color: Int = DividerDefaults.color(),
    thickness: Int = DividerDefaults.thickness(),
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    emit(
        type = NodeType.Divider,
        key = key,
        props = props {
            set(TypedPropKeys.DividerColor, color)
            set(TypedPropKeys.DividerThickness, thickness)
        },
        spec = DividerNodeProps(
            color = color,
            thickness = thickness,
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.Row(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    verticalAlignment: VerticalAlignment = VerticalAlignment.Top,
    modifier: Modifier = Modifier,
    content: RowScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Row,
        key = key,
        props = props {
            set(TypedPropKeys.LinearSpacing, spacing)
            set(TypedPropKeys.RowMainAxisArrangement, arrangement)
            set(TypedPropKeys.RowVerticalAlignment, verticalAlignment)
        },
        spec = RowNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            verticalAlignment = verticalAlignment,
        ),
        modifier = modifier,
        children = RowScope().apply(content).build(),
    )
}

fun UiTreeBuilder.Column(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    modifier: Modifier = Modifier,
    content: ColumnScope.() -> Unit,
) {
    emitResolved(
        type = NodeType.Column,
        key = key,
        props = props {
            set(TypedPropKeys.LinearSpacing, spacing)
            set(TypedPropKeys.ColumnMainAxisArrangement, arrangement)
            set(TypedPropKeys.ColumnHorizontalAlignment, horizontalAlignment)
        },
        spec = ColumnNodeProps(
            spacing = spacing,
            arrangement = arrangement,
            horizontalAlignment = horizontalAlignment,
        ),
        modifier = modifier,
        children = ColumnScope().apply(content).build(),
    )
}

fun <T> UiTreeBuilder.LazyColumn(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    spacing: Int = 0,
    modifier: Modifier = Modifier,
    itemContent: UiTreeBuilder.(T) -> Unit,
) {
    val localSnapshot = LocalContext.snapshot()
    val resolvedItems = items.map { item ->
        LazyListItem(
            key = key?.invoke(item),
            contentToken = item,
            sessionFactory = LazyListItemSessionFactory { container ->
                WidgetLazyListItemSession(
                    container = container,
                    localSnapshot = localSnapshot,
                    content = {
                        itemContent(item)
                    },
                )
            },
            sessionUpdater = { session ->
                (session as? WidgetLazyListItemSession)?.updateContent(
                    localSnapshot = localSnapshot,
                    content = {
                        itemContent(item)
                    },
                )
            },
        )
    }
    emit(
        type = NodeType.LazyColumn,
        props = props {
            set(TypedPropKeys.LazyContentPadding, contentPadding)
            set(TypedPropKeys.LazySpacing, spacing)
            set(TypedPropKeys.LazyItems, resolvedItems)
        },
        spec = LazyColumnNodeProps(
            contentPadding = contentPadding,
            spacing = spacing,
            items = resolvedItems,
        ),
        modifier = modifier,
    )
}

@UiDslMarker
class TabPagerScope internal constructor() {
    private val pages = mutableListOf<TabPagerPage>()

    fun Page(
        title: String,
        key: Any? = title,
        contentToken: Any? = title,
        content: UiTreeBuilder.() -> Unit,
    ) {
        pages += TabPagerPage(
            title = title,
            key = key,
            contentToken = contentToken,
            content = content,
        )
    }

    internal fun build(): List<TabPagerPage> = pages.toList()
}

fun UiTreeBuilder.TabPager(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    key: Any? = null,
    backgroundColor: Int = TabPagerDefaults.backgroundColor(),
    indicatorColor: Int = TabPagerDefaults.indicatorColor(),
    cornerRadius: Int = TabPagerDefaults.cornerRadius(),
    indicatorHeight: Int = TabPagerDefaults.indicatorHeight(),
    tabPaddingHorizontal: Int = TabPagerDefaults.tabPaddingHorizontal(),
    tabPaddingVertical: Int = TabPagerDefaults.tabPaddingVertical(),
    selectedTextColor: Int = TabPagerDefaults.selectedTextColor(),
    unselectedTextColor: Int = TabPagerDefaults.unselectedTextColor(),
    rippleColor: Int = TabPagerDefaults.rippleColor(),
    modifier: Modifier = Modifier,
    pages: TabPagerScope.() -> Unit,
) {
    val builtPages = TabPagerScope().apply(pages).build()
    val localSnapshot = LocalContext.snapshot()
    val resolvedPages = builtPages.map { page ->
        TabPage(
            title = page.title,
            item = LazyListItem(
                key = page.key,
                contentToken = page.contentToken,
                sessionFactory = LazyListItemSessionFactory { container ->
                    WidgetLazyListItemSession(
                        container = container,
                        localSnapshot = localSnapshot,
                        content = page.content,
                    )
                },
                sessionUpdater = { session ->
                    (session as? WidgetLazyListItemSession)?.updateContent(
                        localSnapshot = localSnapshot,
                        content = page.content,
                    )
                },
            ),
        )
    }
    emit(
        type = NodeType.TabPager,
        key = key,
        props = props {
            set(TypedPropKeys.SelectedTabIndex, selectedTabIndex)
            set(TypedPropKeys.OnTabSelected, onTabSelected)
            set(TypedPropKeys.TabBackgroundColor, backgroundColor)
            set(TypedPropKeys.TabIndicatorColor, indicatorColor)
            set(TypedPropKeys.TabCornerRadius, cornerRadius)
            set(TypedPropKeys.TabIndicatorHeight, indicatorHeight)
            set(TypedPropKeys.TabContentPaddingHorizontal, tabPaddingHorizontal)
            set(TypedPropKeys.TabContentPaddingVertical, tabPaddingVertical)
            set(TypedPropKeys.TabSelectedTextColor, selectedTextColor)
            set(TypedPropKeys.TabUnselectedTextColor, unselectedTextColor)
            set(TypedPropKeys.TabRippleColor, rippleColor)
            set(TypedPropKeys.TabPages, resolvedPages)
        },
        spec = TabPagerNodeProps(
            pages = resolvedPages,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected,
            backgroundColor = backgroundColor,
            indicatorColor = indicatorColor,
            cornerRadius = cornerRadius,
            indicatorHeight = indicatorHeight,
            tabPaddingHorizontal = tabPaddingHorizontal,
            tabPaddingVertical = tabPaddingVertical,
            selectedTextColor = selectedTextColor,
            unselectedTextColor = unselectedTextColor,
            rippleColor = rippleColor,
        ),
        modifier = modifier,
    )
}

private class WidgetLazyListItemSession(
    container: android.view.ViewGroup,
    localSnapshot: LocalSnapshot,
    content: UiTreeBuilder.() -> Unit,
) : LazyListItemSession {
    private var capturedLocals = localSnapshot
    private var renderContent = content
    private val session = RenderSession(
        container = container,
        content = {
            LocalContext.withSnapshot(capturedLocals) {
                renderContent()
            }
        },
    )

    override fun render() {
        session.render()
    }

    override fun dispose() {
        session.dispose()
    }

    fun updateContent(
        localSnapshot: LocalSnapshot,
        content: UiTreeBuilder.() -> Unit,
    ) {
        capturedLocals = localSnapshot
        renderContent = content
    }
}

internal data class TabPagerPage(
    val title: String,
    val key: Any?,
    val contentToken: Any?,
    val content: UiTreeBuilder.() -> Unit,
)
