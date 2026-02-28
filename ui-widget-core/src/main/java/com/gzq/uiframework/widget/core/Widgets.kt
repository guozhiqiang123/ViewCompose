package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.alpha
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.border
import com.gzq.uiframework.renderer.modifier.clickable
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.fillMaxWidth
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.minHeight
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.rippleColor
import com.gzq.uiframework.renderer.modifier.size
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.textSize
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.node.ImageContentScale
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TextAlign
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.TextOverflow

fun UiTreeBuilder.Text(
    text: String,
    style: UiTextStyle = TextDefaults.bodyStyle(),
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign = TextAlign.Start,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Text,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
                PropKeys.TEXT_MAX_LINES to maxLines,
                PropKeys.TEXT_OVERFLOW to overflow,
                PropKeys.TEXT_ALIGN to textAlign,
            ),
        ),
        modifier = Modifier.Empty
            .textColor(TextDefaults.primaryColor())
            .textSize(style.fontSizeSp)
            .then(modifier),
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
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Image,
        key = key,
        props = Props(
            values = buildMap {
                put(PropKeys.IMAGE_SOURCE, source)
                put(PropKeys.IMAGE_CONTENT_SCALE, contentScale)
                put(PropKeys.IMAGE_CONTENT_DESCRIPTION, contentDescription)
                put(PropKeys.IMAGE_REMOTE_LOADER, ImageLoading.current)
                tint?.let { put(PropKeys.IMAGE_TINT, it) }
                placeholder?.let { put(PropKeys.IMAGE_PLACEHOLDER, it) }
                error?.let { put(PropKeys.IMAGE_ERROR, it) }
                fallback?.let { put(PropKeys.IMAGE_FALLBACK, it) }
            },
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
    modifier: Modifier = Modifier.Empty,
) {
    Image(
        source = source,
        contentDescription = contentDescription,
        contentScale = ImageContentScale.Inside,
        tint = tint,
        key = key,
        modifier = Modifier.Empty
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
    modifier: Modifier = Modifier.Empty,
) {
    val defaultModifier = Modifier.Empty
        .padding(
            horizontal = TextFieldDefaults.horizontalPadding(size),
            vertical = TextFieldDefaults.verticalPadding(size),
        )
    val sizeModifier = if (singleLine) {
        defaultModifier.height(TextFieldDefaults.height(size))
    } else {
        defaultModifier
    }
    emit(
        type = NodeType.TextField,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.VALUE to value,
                PropKeys.ON_VALUE_CHANGE to onValueChange,
                PropKeys.HINT to hint,
                PropKeys.LABEL to label,
                PropKeys.PLACEHOLDER to placeholder,
                PropKeys.SUPPORTING_TEXT to supportingText,
                PropKeys.SINGLE_LINE to singleLine,
                PropKeys.TEXT_FIELD_TYPE to keyboardType,
                PropKeys.READ_ONLY to readOnly,
                PropKeys.MAX_LINES to maxLines,
                PropKeys.MIN_LINES to minLines,
                PropKeys.IME_ACTION to imeAction,
                PropKeys.ENABLED to enabled,
                PropKeys.IS_ERROR to isError,
                PropKeys.HINT_TEXT_COLOR to TextFieldDefaults.hintColor(
                    enabled = enabled,
                    isError = isError,
                ),
                PropKeys.LABEL_TEXT_COLOR to TextFieldDefaults.labelColor(
                    enabled = enabled,
                    isError = isError,
                ),
                PropKeys.SUPPORTING_TEXT_COLOR to TextFieldDefaults.supportingTextColor(
                    enabled = enabled,
                    isError = isError,
                ),
                PropKeys.LABEL_TEXT_SIZE_SP to TextFieldDefaults.labelTextStyle().fontSizeSp,
                PropKeys.SUPPORTING_TEXT_SIZE_SP to TextFieldDefaults.supportingTextStyle().fontSizeSp,
            ),
        ),
        modifier = Modifier.Empty
            .then(sizeModifier)
            .backgroundColor(
                TextFieldDefaults.containerColor(
                    variant = variant,
                    enabled = enabled,
                    isError = isError,
                ),
            )
            .border(
                width = TextFieldDefaults.borderWidth(variant),
                color = TextFieldDefaults.borderColor(
                    variant = variant,
                    enabled = enabled,
                    isError = isError,
                ),
            )
            .cornerRadius(TextFieldDefaults.cornerRadius())
            .rippleColor(TextFieldDefaults.pressedColor())
            .textColor(TextFieldDefaults.textColor(enabled))
            .textSize(style.fontSizeSp)
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
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
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
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.checkboxLabelColor(enabled))
            .rippleColor(InputControlDefaults.pressedColor())
            .textSize(style.fontSizeSp)
            .then(modifier),
    )
}

fun UiTreeBuilder.Switch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
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
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.switchLabelColor(enabled))
            .rippleColor(InputControlDefaults.pressedColor())
            .textSize(style.fontSizeSp)
            .then(modifier),
    )
}

fun UiTreeBuilder.RadioButton(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    style: UiTextStyle = InputControlDefaults.labelStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
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
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.radioButtonLabelColor(enabled))
            .rippleColor(InputControlDefaults.pressedColor())
            .textSize(style.fontSizeSp)
            .then(modifier),
    )
}

fun UiTreeBuilder.Slider(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = 0,
    max: Int = 100,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
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
        modifier = Modifier.Empty
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
    modifier: Modifier = Modifier.Empty,
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
        modifier = Modifier.Empty
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
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Button,
        key = key,
        props = Props(
            values = buildMap {
                put(PropKeys.TEXT, text)
                put(PropKeys.ON_CLICK, onClick)
                put(PropKeys.ENABLED, enabled)
                put(PropKeys.BUTTON_ICON_SIZE, ButtonDefaults.iconSize(size))
                put(PropKeys.BUTTON_ICON_SPACING, ButtonDefaults.iconSpacing(size))
                leadingIcon?.let { put(PropKeys.BUTTON_LEADING_ICON, it) }
                trailingIcon?.let { put(PropKeys.BUTTON_TRAILING_ICON, it) }
            },
        ),
        modifier = Modifier.Empty
            .minHeight(ButtonDefaults.height(size))
            .padding(
                horizontal = ButtonDefaults.horizontalPadding(size),
                vertical = ButtonDefaults.verticalPadding(size),
            )
            .backgroundColor(ButtonDefaults.containerColor(variant, enabled))
            .border(
                width = ButtonDefaults.borderWidth(variant),
                color = ButtonDefaults.borderColor(variant, enabled),
            )
            .cornerRadius(ButtonDefaults.cornerRadius())
            .rippleColor(ButtonDefaults.pressedColor())
            .textColor(ButtonDefaults.contentColor(variant, enabled))
            .textSize(style.fontSizeSp)
            .then(modifier),
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
    modifier: Modifier = Modifier.Empty,
) {
    val semanticModifier = Modifier.Empty
        .size(
            width = IconButtonDefaults.size(size),
            height = IconButtonDefaults.size(size),
        )
        .padding(IconButtonDefaults.contentPadding(size))
        .backgroundColor(IconButtonDefaults.containerColor(variant, enabled))
        .border(
            width = IconButtonDefaults.borderWidth(variant),
            color = IconButtonDefaults.borderColor(variant, enabled),
        )
        .cornerRadius(IconButtonDefaults.cornerRadius())
        .rippleColor(IconButtonDefaults.pressedColor())
        .then(
            if (enabled && onClick != null) {
                Modifier.Empty.clickable(onClick)
            } else {
                Modifier.Empty
            },
        )
        .then(modifier)
    emit(
        type = NodeType.IconButton,
        key = key,
        props = Props(
            values = buildMap {
                put(PropKeys.IMAGE_SOURCE, icon)
                put(PropKeys.IMAGE_CONTENT_DESCRIPTION, contentDescription)
                put(PropKeys.IMAGE_CONTENT_SCALE, ImageContentScale.Inside)
                put(PropKeys.IMAGE_TINT, IconButtonDefaults.contentColor(variant, enabled))
                put(PropKeys.ON_CLICK, onClick)
                put(PropKeys.ENABLED, enabled)
                put(PropKeys.IMAGE_REMOTE_LOADER, ImageLoading.current)
            },
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
    modifier: Modifier = Modifier.Empty,
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
        modifier = Modifier.Empty
            .height(SegmentedControlDefaults.height(size))
            .then(modifier),
    )
}

fun UiTreeBuilder.AndroidView(
    factory: (Context) -> View,
    update: (View) -> Unit = {},
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
        type = NodeType.Box,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.BOX_ALIGNMENT to contentAlignment,
            ),
        ),
        modifier = modifier,
        content = content,
    )
}

fun UiTreeBuilder.Surface(
    key: Any? = null,
    variant: SurfaceVariant = SurfaceVariant.Default,
    enabled: Boolean = true,
    contentAlignment: BoxAlignment = BoxAlignment.TopStart,
    contentColor: Int = SurfaceDefaults.contentColor(variant),
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    val semanticModifier = Modifier.Empty
        .backgroundColor(SurfaceDefaults.backgroundColor(variant))
        .cornerRadius(SurfaceDefaults.cardCornerRadius())
        .rippleColor(SurfaceDefaults.pressedColor())
        .then(
            if (enabled) {
                Modifier.Empty
            } else {
                Modifier.Empty.alpha(SurfaceDefaults.disabledAlpha())
            },
        )
        .then(
            if (enabled && onClick != null) {
                Modifier.Empty.clickable(onClick)
            } else {
                Modifier.Empty
            },
        )
        .then(modifier)
    ProvideContentColor(contentColor) {
        emit(
            type = NodeType.Surface,
            key = key,
            props = Props(
                values = mapOf(
                    PropKeys.BOX_ALIGNMENT to contentAlignment,
                ),
            ),
            modifier = semanticModifier,
            content = content,
        )
    }
}

fun UiTreeBuilder.Spacer(
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Spacer,
        key = key,
        modifier = modifier,
    )
}

fun UiTreeBuilder.FlexibleSpacer(
    weight: Float = 1f,
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    Spacer(
        key = key,
        modifier = modifier.weight(weight),
    )
}

fun UiTreeBuilder.Divider(
    color: Int = DividerDefaults.color(),
    thickness: Int = DividerDefaults.thickness(),
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
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
        content = content,
    )
}

fun UiTreeBuilder.Column(
    key: Any? = null,
    spacing: Int = 0,
    arrangement: MainAxisArrangement = MainAxisArrangement.Start,
    horizontalAlignment: HorizontalAlignment = HorizontalAlignment.Start,
    modifier: Modifier = Modifier.Empty,
    content: UiTreeBuilder.() -> Unit,
) {
    emit(
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
        content = content,
    )
}

fun <T> UiTreeBuilder.LazyColumn(
    items: List<T>,
    key: ((T) -> Any)? = null,
    contentPadding: Int = 0,
    spacing: Int = 0,
    modifier: Modifier = Modifier.Empty,
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
    modifier: Modifier = Modifier.Empty,
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
