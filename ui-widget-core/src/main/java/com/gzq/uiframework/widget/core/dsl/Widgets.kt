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
    val sizeModifier = if (singleLine) {
        Modifier.height(TextFieldDefaults.height(size))
    } else {
        Modifier
    }
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
            setRaw(PropKeys.IS_ERROR, isError)
            set(TypedPropKeys.TextColor, TextFieldDefaults.textColor(enabled))
            set(TypedPropKeys.TextSizeSp, style.fontSizeSp)
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
            set(
                TypedPropKeys.HintTextColor,
                TextFieldDefaults.hintColor(
                    enabled = enabled,
                    isError = isError,
                ),
            )
            set(
                TypedPropKeys.LabelTextColor,
                TextFieldDefaults.labelColor(
                    enabled = enabled,
                    isError = isError,
                ),
            )
            set(
                TypedPropKeys.SupportingTextColor,
                TextFieldDefaults.supportingTextColor(
                    enabled = enabled,
                    isError = isError,
                ),
            )
            set(TypedPropKeys.LabelTextSizeSp, TextFieldDefaults.labelTextStyle().fontSizeSp)
            set(TypedPropKeys.SupportingTextSizeSp, TextFieldDefaults.supportingTextStyle().fontSizeSp)
        },
        modifier = Modifier
            .then(sizeModifier)
            .then(modifier),
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
    emit(
        type = NodeType.Checkbox,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
                PropKeys.CHECKED to checked,
                PropKeys.ON_CHECKED_CHANGE to onCheckedChange,
                PropKeys.ENABLED to enabled,
                PropKeys.CONTROL_COLOR to InputControlDefaults.checkboxControlColor(enabled),
                PropKeys.TEXT_COLOR to InputControlDefaults.checkboxLabelColor(enabled),
                PropKeys.TEXT_SIZE_SP to style.fontSizeSp,
                PropKeys.STYLE_RIPPLE_COLOR to InputControlDefaults.pressedColor(),
            ),
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
    emit(
        type = NodeType.Switch,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
                PropKeys.CHECKED to checked,
                PropKeys.ON_CHECKED_CHANGE to onCheckedChange,
                PropKeys.ENABLED to enabled,
                PropKeys.CONTROL_COLOR to InputControlDefaults.switchControlColor(enabled),
                PropKeys.TEXT_COLOR to InputControlDefaults.switchLabelColor(enabled),
                PropKeys.TEXT_SIZE_SP to style.fontSizeSp,
                PropKeys.STYLE_RIPPLE_COLOR to InputControlDefaults.pressedColor(),
            ),
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
    emit(
        type = NodeType.RadioButton,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
                PropKeys.CHECKED to checked,
                PropKeys.ON_CHECKED_CHANGE to onCheckedChange,
                PropKeys.ENABLED to enabled,
                PropKeys.CONTROL_COLOR to InputControlDefaults.radioButtonControlColor(enabled),
                PropKeys.TEXT_COLOR to InputControlDefaults.radioButtonLabelColor(enabled),
                PropKeys.TEXT_SIZE_SP to style.fontSizeSp,
                PropKeys.STYLE_RIPPLE_COLOR to InputControlDefaults.pressedColor(),
            ),
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
    emit(
        type = NodeType.Slider,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.SLIDER_VALUE to value,
                PropKeys.MIN_VALUE to min,
                PropKeys.MAX_VALUE to max,
                PropKeys.ENABLED to enabled,
                PropKeys.CONTROL_COLOR to InputControlDefaults.sliderControlColor(enabled),
                PropKeys.ON_SLIDER_VALUE_CHANGE to onValueChange,
            ),
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
        props = Props(
            values = mapOf(
                PropKeys.PROGRESS_FRACTION to progress,
                PropKeys.PROGRESS_INDICATOR_COLOR to indicatorColor,
                PropKeys.PROGRESS_TRACK_COLOR to trackColor,
                PropKeys.PROGRESS_TRACK_THICKNESS to trackThickness,
            ),
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
        props = Props(
            values = mapOf(
                PropKeys.PROGRESS_FRACTION to progress,
                PropKeys.PROGRESS_INDICATOR_COLOR to indicatorColor,
                PropKeys.PROGRESS_TRACK_COLOR to trackColor,
                PropKeys.PROGRESS_TRACK_THICKNESS to trackThickness,
                PropKeys.PROGRESS_INDICATOR_SIZE to size,
            ),
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
    emit(
        type = NodeType.Button,
        key = key,
        props = props {
            set(TypedPropKeys.Text, text)
            set(TypedPropKeys.OnClick, onClick)
            set(TypedPropKeys.Enabled, enabled)
            set(TypedPropKeys.TextColor, ButtonDefaults.contentColor(variant, enabled))
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
            set(TypedPropKeys.ButtonIconSize, ButtonDefaults.iconSize(size))
            set(TypedPropKeys.ButtonIconSpacing, ButtonDefaults.iconSpacing(size))
            set(TypedPropKeys.ButtonLeadingIcon, leadingIcon)
            set(TypedPropKeys.ButtonTrailingIcon, trailingIcon)
        },
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
            set(TypedPropKeys.ImageTint, IconButtonDefaults.contentColor(variant, enabled))
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
    emit(
        type = NodeType.SegmentedControl,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.SEGMENT_ITEMS to items.map { label ->
                    SegmentedControlItem(label = label)
                },
                PropKeys.SEGMENT_SELECTED_INDEX to selectedIndex,
                PropKeys.ON_SEGMENT_SELECTED to onSelectionChange,
                PropKeys.ENABLED to enabled,
                PropKeys.SEGMENT_BACKGROUND_COLOR to SegmentedControlDefaults.backgroundColor(enabled),
                PropKeys.SEGMENT_INDICATOR_COLOR to SegmentedControlDefaults.indicatorColor(enabled),
                PropKeys.SEGMENT_CORNER_RADIUS to SegmentedControlDefaults.cornerRadius(),
                PropKeys.SEGMENT_TEXT_COLOR to SegmentedControlDefaults.textColor(enabled),
                PropKeys.SEGMENT_SELECTED_TEXT_COLOR to SegmentedControlDefaults.selectedTextColor(enabled),
                PropKeys.SEGMENT_RIPPLE_COLOR to SegmentedControlDefaults.rippleColor(enabled),
                PropKeys.SEGMENT_TEXT_SIZE_SP to SegmentedControlDefaults.textStyle(size).fontSizeSp,
                PropKeys.SEGMENT_CONTENT_PADDING_HORIZONTAL to SegmentedControlDefaults.horizontalPadding(size),
                PropKeys.SEGMENT_CONTENT_PADDING_VERTICAL to SegmentedControlDefaults.verticalPadding(size),
            ),
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
        props = Props(
            values = mapOf(
                PropKeys.VIEW_FACTORY to factory,
                PropKeys.VIEW_UPDATE to update,
            ),
        ),
        modifier = modifier,
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
        props = Props(
            values = mapOf(
                PropKeys.BOX_ALIGNMENT to contentAlignment,
            ),
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
            props = Props(
                values = buildMap {
                    put(PropKeys.BOX_ALIGNMENT, contentAlignment)
                    put(PropKeys.STYLE_BACKGROUND_COLOR, SurfaceDefaults.backgroundColor(variant))
                    put(PropKeys.STYLE_CORNER_RADIUS, SurfaceDefaults.cardCornerRadius())
                    put(PropKeys.STYLE_RIPPLE_COLOR, SurfaceDefaults.pressedColor())
                    if (!enabled) {
                        put(PropKeys.STYLE_ALPHA, SurfaceDefaults.disabledAlpha())
                    }
                },
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
        props = Props(
            values = mapOf(
                PropKeys.DIVIDER_COLOR to color,
                PropKeys.DIVIDER_THICKNESS to thickness,
            ),
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
        props = Props(
            values = mapOf(
                PropKeys.LINEAR_SPACING to spacing,
                PropKeys.ROW_MAIN_AXIS_ARRANGEMENT to arrangement,
                PropKeys.ROW_VERTICAL_ALIGNMENT to verticalAlignment,
            ),
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
        props = Props(
            values = mapOf(
                PropKeys.LINEAR_SPACING to spacing,
                PropKeys.COLUMN_MAIN_AXIS_ARRANGEMENT to arrangement,
                PropKeys.COLUMN_HORIZONTAL_ALIGNMENT to horizontalAlignment,
            ),
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
    emit(
        type = NodeType.LazyColumn,
        props = Props(
            values = mapOf(
                PropKeys.LAZY_CONTENT_PADDING to contentPadding,
                PropKeys.LAZY_SPACING to spacing,
                PropKeys.LAZY_ITEMS to items.map { item ->
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
                },
            ),
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
    emit(
        type = NodeType.TabPager,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.SELECTED_TAB_INDEX to selectedTabIndex,
                PropKeys.ON_TAB_SELECTED to onTabSelected,
                PropKeys.TAB_BACKGROUND_COLOR to backgroundColor,
                PropKeys.TAB_INDICATOR_COLOR to indicatorColor,
                PropKeys.TAB_CORNER_RADIUS to cornerRadius,
                PropKeys.TAB_INDICATOR_HEIGHT to indicatorHeight,
                PropKeys.TAB_CONTENT_PADDING_HORIZONTAL to tabPaddingHorizontal,
                PropKeys.TAB_CONTENT_PADDING_VERTICAL to tabPaddingVertical,
                PropKeys.TAB_SELECTED_TEXT_COLOR to selectedTextColor,
                PropKeys.TAB_UNSELECTED_TEXT_COLOR to unselectedTextColor,
                PropKeys.TAB_RIPPLE_COLOR to rippleColor,
                PropKeys.TAB_PAGES to builtPages.map { page ->
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
                },
            ),
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
