package com.viewcompose.widget.core

import com.viewcompose.renderer.layout.VerticalAlignment
import com.viewcompose.renderer.modifier.Modifier
import com.viewcompose.renderer.modifier.backgroundColor
import com.viewcompose.renderer.modifier.clip
import com.viewcompose.renderer.modifier.cornerRadius
import com.viewcompose.renderer.modifier.elevation
import com.viewcompose.renderer.modifier.height
import com.viewcompose.renderer.modifier.padding
import com.viewcompose.renderer.node.ImageSource
import com.viewcompose.renderer.node.TextFieldImeAction
import com.viewcompose.renderer.node.TextFieldType

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
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = placeholder,
            enabled = enabled,
            singleLine = true,
            minLines = 1,
            maxLines = 1,
            keyboardType = TextFieldType.Text,
            imeAction = if (onSearch != null) TextFieldImeAction.Search else TextFieldImeAction.Default,
            hintColor = SearchBarDefaults.placeholderColor(),
            textColor = SearchBarDefaults.contentColor(),
            textStyle = SearchBarDefaults.textStyle(),
            backgroundColor = 0x00000000,
            borderWidth = 0,
            borderColor = 0x00000000,
            cornerRadius = 0,
            rippleColor = 0,
            minHeight = 0,
            paddingHorizontal = 0,
            paddingVertical = 0,
            modifier = Modifier.weight(1f),
        )
        if (trailingIcon != null) {
            trailingIcon()
        }
    }
}
