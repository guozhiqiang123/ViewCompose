package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.spec.SliderNodeProps
import com.gzq.uiframework.renderer.node.spec.ToggleNodeProps

fun UiTreeBuilder.Checkbox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    checkedColor: Int = InputControlDefaults.checkboxCheckedColor(enabled),
    uncheckedColor: Int = InputControlDefaults.checkboxUncheckedColor(enabled),
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
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
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
    checkedColor: Int = InputControlDefaults.radioButtonCheckedColor(enabled),
    uncheckedColor: Int = InputControlDefaults.radioButtonUncheckedColor(enabled),
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
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
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
