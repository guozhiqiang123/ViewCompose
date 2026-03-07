package com.gzq.uiframework.widget.core

import com.gzq.uiframework.renderer.layout.VerticalAlignment
import com.gzq.uiframework.renderer.modifier.Modifier
import com.gzq.uiframework.renderer.modifier.backgroundColor
import com.gzq.uiframework.renderer.modifier.clip
import com.gzq.uiframework.renderer.modifier.cornerRadius
import com.gzq.uiframework.renderer.modifier.elevation
import com.gzq.uiframework.renderer.modifier.height
import com.gzq.uiframework.renderer.modifier.padding
import com.gzq.uiframework.renderer.node.ImageSource
import com.gzq.uiframework.renderer.node.NodeType
import com.gzq.uiframework.renderer.node.TextFieldImeAction
import com.gzq.uiframework.renderer.node.TextFieldType
import com.gzq.uiframework.renderer.node.spec.TextFieldNodeProps

fun UiTreeBuilder.SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: ((String) -> Unit)? = null,
    placeholder: String = "",
    leadingIcon: ImageSource? = null,
    trailingIcon: (UiTreeBuilder.() -> Unit)? = null,
    enabled: Boolean = true,
    key: Any? = null,
    modifier: Modifier = Modifier,
) {
    val containerColor = SearchBarDefaults.containerColor()
    val radius = SearchBarDefaults.cornerRadius()
    val semanticModifier = Modifier
        .height(SearchBarDefaults.height())
        .backgroundColor(containerColor)
        .cornerRadius(radius)
        .clip()
        .elevation(SearchBarDefaults.elevation())
        .padding(horizontal = SearchBarDefaults.horizontalPadding())
        .then(modifier)
    Row(
        key = key,
        spacing = SearchBarDefaults.iconSpacing(),
        verticalAlignment = VerticalAlignment.Center,
        modifier = semanticModifier,
    ) {
        if (leadingIcon != null) {
            Icon(
                source = leadingIcon,
                tint = SearchBarDefaults.iconColor(),
                size = SearchBarDefaults.iconSize(),
            )
        }
        emit(
            type = NodeType.TextField,
            spec = TextFieldNodeProps(
                value = query,
                label = "",
                labelColor = 0,
                labelTextSizeSp = 0,
                supportingText = "",
                supportingTextColor = 0,
                supportingTextSizeSp = 0,
                placeholder = placeholder,
                enabled = enabled,
                singleLine = true,
                minLines = 1,
                maxLines = 1,
                keyboardType = TextFieldType.Text,
                imeAction = if (onSearch != null) TextFieldImeAction.Search else TextFieldImeAction.Default,
                hintColor = SearchBarDefaults.placeholderColor(),
                readOnly = false,
                onValueChange = onQueryChange,
                textColor = SearchBarDefaults.contentColor(),
                textSizeSp = SearchBarDefaults.textStyle().fontSizeSp,
                backgroundColor = 0x00000000,
                borderWidth = 0,
                borderColor = 0x00000000,
                cornerRadius = 0,
                rippleColor = 0,
                minHeight = 0,
                paddingHorizontal = 0,
                paddingVertical = 0,
            ),
            modifier = Modifier.weight(1f),
        )
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}
