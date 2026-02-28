package com.gzq.uiframework.widget.core

import android.content.Context
import android.view.View
import com.gzq.uiframework.renderer.layout.BoxAlignment
import com.gzq.uiframework.renderer.layout.HorizontalAlignment
import com.gzq.uiframework.renderer.layout.MainAxisArrangement
import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.border
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.modifier.rippleColor
import com.gzq.uiframework.renderer.modifier.textColor
import com.gzq.uiframework.renderer.modifier.textSize
import com.gzq.uiframework.renderer.modifier.weight
import com.gzq.uiframework.renderer.node.LazyListItem
import com.gzq.uiframework.renderer.node.LazyListItemSession
import com.gzq.uiframework.renderer.node.LazyListItemSessionFactory
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.PropKeys
import com.gzq.uiframework.renderer.node.Props
import com.gzq.uiframework.renderer.node.SegmentedControlItem
import com.gzq.uiframework.renderer.node.TabPage
import com.gzq.uiframework.renderer.node.TextFieldType

fun UiTreeBuilder.Text(
    text: String,
    style: UiTextStyle = TextDefaults.bodyStyle(),
    key: Any? = null,
    modifier: Modifier = Modifier.Empty,
) {
    emit(
        type = NodeType.Text,
        key = key,
        props = Props(
            values = mapOf(
                PropKeys.TEXT to text,
            ),
        ),
        modifier = Modifier.Empty
            .textColor(TextDefaults.primaryColor())
            .textSize(style.fontSizeSp)
            .then(modifier),
    )
}

fun UiTreeBuilder.TextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    singleLine: Boolean = true,
    type: TextFieldType = TextFieldType.Text,
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
                PropKeys.SINGLE_LINE to singleLine,
                PropKeys.TEXT_FIELD_TYPE to type,
                PropKeys.ENABLED to enabled,
                PropKeys.IS_ERROR to isError,
                PropKeys.HINT_TEXT_COLOR to TextFieldDefaults.hintColor(
                    enabled = enabled,
                    isError = isError,
                ),
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
        singleLine = true,
        type = TextFieldType.Password,
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
                PropKeys.CONTROL_COLOR to InputControlDefaults.controlColor(enabled),
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.labelColor(enabled))
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
                PropKeys.CONTROL_COLOR to InputControlDefaults.controlColor(enabled),
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.labelColor(enabled))
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
                PropKeys.CONTROL_COLOR to InputControlDefaults.controlColor(enabled),
            ),
        ),
        modifier = Modifier.Empty
            .textColor(InputControlDefaults.labelColor(enabled))
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
                PropKeys.CONTROL_COLOR to InputControlDefaults.controlColor(enabled),
                PropKeys.ON_SLIDER_VALUE_CHANGE to onValueChange,
            ),
        ),
        modifier = modifier,
    )
}

fun UiTreeBuilder.EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
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
        singleLine = true,
        type = TextFieldType.Email,
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
        singleLine = true,
        type = TextFieldType.Number,
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
        singleLine = false,
        type = TextFieldType.Text,
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
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
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
            },
        ),
        modifier = Modifier.Empty
            .height(ButtonDefaults.height(size))
            .padding(
                horizontal = ButtonDefaults.horizontalPadding(size),
                vertical = ButtonDefaults.verticalPadding(size),
            )
            .backgroundColor(ButtonDefaults.containerColor(variant))
            .border(
                width = ButtonDefaults.borderWidth(variant),
                color = ButtonDefaults.borderColor(variant),
            )
            .cornerRadius(ButtonDefaults.cornerRadius())
            .rippleColor(ButtonDefaults.pressedColor())
            .textColor(ButtonDefaults.contentColor(variant))
            .textSize(style.fontSizeSp)
            .then(modifier),
    )
}

fun UiTreeBuilder.SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit,
    size: SegmentedControlSize = SegmentedControlSize.Medium,
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
                PropKeys.SEGMENT_BACKGROUND_COLOR to SegmentedControlDefaults.backgroundColor(),
                PropKeys.SEGMENT_INDICATOR_COLOR to SegmentedControlDefaults.indicatorColor(),
                PropKeys.SEGMENT_CORNER_RADIUS to SegmentedControlDefaults.cornerRadius(),
                PropKeys.SEGMENT_TEXT_COLOR to SegmentedControlDefaults.textColor(),
                PropKeys.SEGMENT_SELECTED_TEXT_COLOR to SegmentedControlDefaults.selectedTextColor(),
                PropKeys.SEGMENT_RIPPLE_COLOR to SegmentedControlDefaults.rippleColor(),
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
    modifier: Modifier = Modifier.Empty,
    itemContent: UiTreeBuilder.(T) -> Unit,
) {
    emit(
        type = NodeType.LazyColumn,
        props = Props(
            values = mapOf(
                PropKeys.LAZY_ITEMS to items.map { item ->
                    LazyListItem(
                        key = key?.invoke(item),
                        contentToken = item,
                        sessionFactory = LazyListItemSessionFactory { container ->
                            WidgetLazyListItemSession(
                                container = container,
                                content = {
                                    itemContent(item)
                                },
                            )
                        },
                        sessionUpdater = { session ->
                            (session as? WidgetLazyListItemSession)?.updateContent {
                                itemContent(item)
                            }
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
                                    content = page.content,
                                )
                            },
                            sessionUpdater = { session ->
                                (session as? WidgetLazyListItemSession)?.updateContent(page.content)
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
    content: UiTreeBuilder.() -> Unit,
) : LazyListItemSession {
    private var renderContent = content
    private val session = RenderSession(
        container = container,
        content = {
            renderContent()
        },
    )

    override fun render() {
        session.render()
    }

    override fun dispose() {
        session.dispose()
    }

    fun updateContent(
        content: UiTreeBuilder.() -> Unit,
    ) {
        renderContent = content
    }
}

internal data class TabPagerPage(
    val title: String,
    val key: Any?,
    val contentToken: Any?,
    val content: UiTreeBuilder.() -> Unit,
)
